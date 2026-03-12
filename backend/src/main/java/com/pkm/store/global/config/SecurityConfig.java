package com.pkm.store.global.config;

import com.pkm.store.global.jwt.JwtFilter; // [추가]
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // [추가]

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // [추가] 우리가 만든 문지기 고용!

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // 로컬 개발 시에는 CSRF를 꺼두는게 편합니다.
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // 1. 아래 경로들은 로그인 없이도 누구나 접근 가능하게 허용합니다.
            .requestMatchers(
                "/",
                "/api/members/login", 
                "/api/members/signup",
                "/api/products/**",      // 상품 목록은 구경할 수 있어야 하니까요.
                "/v3/api-docs/**",       // Swagger용 경로
                "/swagger-ui/**",        // Swagger UI용 경로
                "/swagger-ui.html",
                "/uploads/**"            // 상품 이미지 경로
            ).permitAll()
            
            // 2. 관리자 페이지는 ADMIN 권한이 있어야만 함
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
            // 3. 나머지는 인증(로그인)이 필요함
            .anyRequest().authenticated()
        )
            // [★핵심 추가★] 기본 아이디/비밀번호 검사기(UsernamePasswordAuthenticationFilter)가 작동하기 전에
            // 우리가 만든 JWT 문지기(jwtFilter)를 먼저 세우겠다는 뜻입니다!
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}