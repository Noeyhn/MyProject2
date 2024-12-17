package com.github.cupangclone.repository.redis;

public interface RedisRepository {

    void saveRedisToToken(String token);
    boolean findRedisToToken(String token);

}
