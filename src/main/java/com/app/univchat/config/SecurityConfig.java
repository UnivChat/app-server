package com.app.univchat.config;

import com.app.univchat.jwt.JwtAccessDeniedHandler;
import com.app.univchat.jwt.JwtAuthenticationEntryPoint;
import com.app.univchat.jwt.JwtProvider;
import com.app.univchat.repository.MemberRepository;
import com.app.univchat.security.filter.LoginFilter;
import com.app.univchat.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity  //SpringSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    private final RedisService redisService;

    //로그인 필터 추가
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(jwtProvider, objectMapper, memberRepository, redisService);
        loginFilter.setAuthenticationManager(authenticationManager()); //spring security에서 제공하는 manager 객체

        return loginFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resource/**", "/css/**", "/js/**", "/img/**", "/lib/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 필터 등록
                .and()
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class) //로그인 필터
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v3/api-docs").permitAll()
                .antMatchers("/hello/**").permitAll()

                .antMatchers("/member/signup","/member/change/password", "/member/check/**","/member/email/verified",
                        "/member/re-token","/api/v1/notification").permitAll()

                // 채팅 테스트 시 주석 해제
                //.antMatchers("/app.js").permitAll()
                //.antMatchers("/main.css").permitAll()

                .antMatchers("/chattingList.html", "/chattingListStyle.css", "/dormChat.html", "/dormChat.js", "/dormChatStyle.css", "/liveChat.js", "/liveChat.html",  "/loveChat.js", "/loveChat.html", "/otoChat.js", "/otoChat.html", "/otoChatRoom.js", "/otoChatRoom.html").permitAll()

                // 채팅 엔드포인트, 기숙사 채팅 기록 조회 인증 x
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/oto/**").permitAll()
                .antMatchers("/chatting/**").permitAll()

                .anyRequest().authenticated() //위의 api가 아닌 경로는 모두 jwt 토큰 인증을 해야 함

                .and()
                .apply(new JwtSecurityConfig(jwtProvider))

        ;
    }
}
