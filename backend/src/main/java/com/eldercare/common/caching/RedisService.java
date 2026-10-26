package com.eldercare.common.caching;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService implements CachingService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, int seconds) {
        this.redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }

    public Object get(String key){
        return this.redisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        this.redisTemplate.delete(key);
    }
}
