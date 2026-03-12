package com.pkm.store.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // 리액트 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 방식
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키나 인증 정보 허용
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프론트엔드에서 http://localhost:8080/uploads/이브이히어로즈.jpg 로 요청하면
        // 백엔드 프로젝트 루트에 있는 uploads 폴더에서 파일을 찾아준다!
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}