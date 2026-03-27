package com.pkm.store.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 도커 환경의 서비스명 'redis' 또는 'localhost'를 주입받아 사용
        config.useSingleServer()
              .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        
        return Redisson.create(config);
    }
}