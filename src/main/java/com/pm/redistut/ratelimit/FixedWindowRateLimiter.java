package com.pm.redistut.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.naming.LimitExceededException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FixedWindowRateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    @SneakyThrows
    public boolean allow(String clientId, int limit, Duration windowSize){
        long windowIndex = System.currentTimeMillis() / windowSize.toMillis();
        String key = "rate:" + clientId + ":" + windowIndex;

        Long countHits = redisTemplate.opsForValue().increment(key);

        if(countHits != null && countHits == 1L){
            redisTemplate.expire(key, windowSize);
        }

        return countHits != null && countHits <= limit ;
    }
}
