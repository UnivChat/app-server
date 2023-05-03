package com.app.univchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redistHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    // Redis 접근을 위한 Connection 팩토리
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redistHost, redisPort);
    }

    // Redis 접근을 위한 객체
    @Bean
    public RedisTemplate<String, ?> redisTemplate() {

        RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 값 확인을 위해 문자열로 직렬화
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // 값 확인을 위해 문자열로 직렬화

        return redisTemplate;
    }
}
