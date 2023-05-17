package com.app.univchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) { //시간 추가
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) { //키값으로 밸류 반환
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }


    public boolean hasKey(String key) { //키값 존재 여부 반환
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void saveToken(String userId, String refreshToken, long time){
        redisTemplate.opsForValue().set(userId, refreshToken, time, TimeUnit.MILLISECONDS);
        //💡 Redis 에 저장 후 만료시간 설정을 위해 자동 삭제처리
    }

    public void setBlackList(String key, Object o, int minutes) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisBlackListTemplate.opsForValue().set(key, o, minutes, TimeUnit.MINUTES);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

    //TODO 레디스에 저장 예시입니다!
    // redisService.saveToken(String.valueOf(userIdx),generateToken.getRefreshToken(), (System.currentTimeMillis()+ refreshTime*1000));
    // redisService.deleteValues(String.valueOf(userId));
    // String expiredAt= redisService.getValues(accessToken);

}
