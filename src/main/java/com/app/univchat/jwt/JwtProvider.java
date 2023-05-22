package com.app.univchat.jwt;

import com.app.univchat.dto.JwtDto;
import com.app.univchat.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private static final String BEARER = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private final Key key;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisService redisService;

    /**
     * 요청 해더에서 액세스 토큰 반환
     * */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expire-time.access-token}") long ACCESS_TOKEN_EXPIRE_TIME,
                       @Value("${jwt.expire-time.refresh-token}") long REFRESH_TOKEN_EXPIRE_TIME) {
        this.ACCESS_TOKEN_EXPIRE_TIME = ACCESS_TOKEN_EXPIRE_TIME;
        this.REFRESH_TOKEN_EXPIRE_TIME = REFRESH_TOKEN_EXPIRE_TIME;

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // jwt 토큰 생성
    private String createToken(String email, long expireTime) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 토큰 발행 유저 정보
                .setIssuedAt(now) // 토큰 발생 시간
                .setExpiration(new Date(now.getTime() + expireTime)) //토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

     //accessToken 발급
    public String createAccessToken(String email){
        return createToken(email, ACCESS_TOKEN_EXPIRE_TIME);
    }

    //refreshToken 발급
    public String createRefreshToken(String email){
        return createToken(email, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     * Token 발급
     */
    public JwtDto generateToken(String email){
        return JwtDto.builder()
                .grantType(BEARER)
                .accessToken(createAccessToken(email))
                .refreshToken(createRefreshToken(email))
                .build();
    }

    /**
     * Token에서 email 추출
     */
    public Authentication getAuthentication(String token){
//        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();  // 기존 claim 생성 code
        String email = getEmail(token);

        // user 객체 생성해 Authentication 객체 반환
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        String username = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();

        return username;
    }



    /**
     * jwt 유효성 검사
     */
    public boolean validateToken(String tokenStr){
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenStr).getBody();

            //토큰 블랙리스트 확인
            if (redisService.hasKey(tokenStr)){
                throw new RuntimeException("로그아웃된 토큰 입니다!");
            }

            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token"); // 유효하지 않은 jwt 토큰
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token"); // 만료된 jwt 토큰
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token"); // 지원하지 않는 jwt 토큰
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty"); // 잘못된 jwt 토큰
        }
        return false;
    }

    //헤더에서 jwt 꺼내오기
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader(AUTHORIZATION_HEADER).substring(7);
    }

}
