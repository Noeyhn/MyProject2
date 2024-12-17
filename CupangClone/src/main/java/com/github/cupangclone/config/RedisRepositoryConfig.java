package com.github.cupangclone.config;

import com.github.cupangclone.properties.RedisProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.Socket;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisProperties.class)
public class RedisRepositoryConfig {

    private final RedisProperties redisProperties;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {

        String host = redisProperties.getHost();
        int port = redisProperties.getPort();

        if ( !isRedisRunning(host, port) ) {
            redisServer = RedisServer
                    .builder()
                    .port(port).setting("maxmemory 128M")
                    .build();
            redisServer.start();
        }

    }

    @PreDestroy
    public void stopRedis() {

        if ( redisServer != null ) {
            redisServer.stop();
        }

    }

    private boolean isRedisRunning(String host, int port){

        try ( Socket socket = new Socket(host, port) ) {
            return true;
        } catch ( IOException e ) {
            return false;
        }

    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;

    }

}
