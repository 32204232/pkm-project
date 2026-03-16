package com.pkm.store.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 실무에서는 클러스터 모드(useClusterServers)를 쓰지만, 초기엔 싱글 서버로 구성합니다.
        config.useSingleServer()
              .setAddress("redis://" + redisHost + ":" + redisPort)
              .setConnectionMinimumIdleSize(10)
              .setConnectionPoolSize(50); // 오픈런 트래픽을 버티기 위한 커넥션 풀 확장
              
        return Redisson.create(config);
    }
}