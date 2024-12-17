package com.github.cupangclone.repository.redis;

import com.github.cupangclone.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository{

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void saveRedisToToken(String token) {

        long remainingTime = jwtTokenProvider.getRemainingTime(token);
        String email = jwtTokenProvider.getUsername(token);

        redisTemplate.opsForValue().set(email, token, remainingTime, TimeUnit.MILLISECONDS);

    }

    @Override
    public boolean findRedisToToken(String token) {

        String email = jwtTokenProvider.getUsername(token);
        return Objects.equals(redisTemplate.opsForValue().get(email), token);

    }
}
