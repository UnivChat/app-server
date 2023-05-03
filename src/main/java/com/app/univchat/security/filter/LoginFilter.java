package com.app.univchat.security.filter;

import com.app.univchat.base.BaseResponse;
import com.app.univchat.base.BaseResponseStatus;
import com.app.univchat.domain.Member;
import com.app.univchat.dto.JwtDto;
import com.app.univchat.dto.LoginDto;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.repository.MemberRepository;
import com.app.univchat.security.auth.PrincipalDetails;
import com.app.univchat.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 로그인
 */

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGIN_URI = "/login";
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    private final RedisService redisService;

    @Value("${jwt.expire-time.refresh-token}")
    private int refreshTime;

    public LoginFilter(JwtProvider jwtProvider, ObjectMapper objectMapper, MemberRepository memberRepository, RedisService redisService) {
        super(LOGIN_URI);
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.memberRepository = memberRepository;
        this.redisService = redisService;
    }

    /**
     * 회원 - Authentication 객체 생성
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("=====login filter=====");

        //메시지 바디 얻기
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        LoginDto.Req loginDto = objectMapper.readValue(messageBody, LoginDto.Req.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        log.info("authToken=" + authToken.getPrincipal());

        //authenticate 객체 생성
        Authentication authenticate = getAuthenticationManager().authenticate(authToken);

        return authenticate;
    }

    /**
     * 로그인 성공
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("=====login success=====");

        //회원정보 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        Member member = principalDetails.getMember();

        //토큰 발급
        JwtDto jwtDto = jwtProvider.generateToken(member.getEmail());

        //TODO: redis에 refresh 토큰 저장
        redisService.saveToken(String.valueOf(member.getEmail()),jwtDto.getRefreshToken(), refreshTime);

        //response Header 설정
        //TODO: accessToken 응답 헤더에? 쿠키에? -> 프론트와 논의
        response.setHeader("Authorization", jwtDto.getAccessToken());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        //response Body 설정
        LoginDto.Res loginResDto = new LoginDto.Res(member, jwtDto);
        BaseResponse<LoginDto.Res> resDto = BaseResponse.ok(BaseResponseStatus.SUCCESS, loginResDto);
        String result = objectMapper.writeValueAsString(resDto);

        response.getWriter().write(result);

//        super.successfulAuthentication(request, response, chain, authResult);
    }

    /**
     * 로그인 실패
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("=====login fail=====");

        //response Header 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");

        //response Body 설정
        BaseResponse<Object> resDto = BaseResponse.ok(BaseResponseStatus.USER_FAILED_TO_LOG_IN_ERROR);
        String result = objectMapper.writeValueAsString(resDto);

        response.getWriter().write(String.valueOf(result));
    }
}
