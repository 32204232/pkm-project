package com.pkm.store.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // [CORS 설정] 리액트 앱(5173 포트)에서 백엔드 API에 접근할 수 있게 허용합니다.
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /* * [참고] addResourceHandlers는 삭제되었습니다. 
     * S3를 사용하므로 더 이상 서버 로컬의 /uploads/** 경로가 필요 없으며, 
     * 도커 환경에서의 무결성을 위해 삭제하는 것이 정석입니다. 삐까!
     */
}