package com.app.univchat.config;

import com.app.univchat.jwt.JwtFilter;
import com.app.univchat.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// custom TokenProvider, JwtFilter를 SecurityConfig에 적용
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtProvider jwtProvider;  // jwt Token provider

    // TokenProvider 를 주입받아서 JwtFilter 를 통해 Security 로직에 필터를 등록
    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(jwtProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class); //custom jwt filter를 Security Filter 앞에 추가
    }
}
