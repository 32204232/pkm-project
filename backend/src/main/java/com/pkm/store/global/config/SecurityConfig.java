package com.pkm.store.global.config;

import com.pkm.store.global.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // CORS 설정 시 필요할 수 있음
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // [★확인★] 이 어노테이션이 있어야 @PreAuthorize가 작동합니다.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // 우리가 만든 JWT 검증 문지기

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // JWT(Stateless) 방식에서는 CSRF 공격 방어가 불필요하므로 끕니다.
            .cors(Customizer.withDefaults()) // React 등 다른 포트와의 통신(CORS)을 허용합니다.
            
            // 세션을 사용하지 않고(Stateless) 토큰으로만 인증하겠다고 선언!
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // 1. 로그인, 회원가입, 상품 구경, 그리고 [★토큰 재발급]은 누구나 접근 가능!
                .requestMatchers(
                    "/",
                    "/api/members/login", 
                    "/api/members/signup",
                    "/api/members/reissue",  // [★핵심 추가★] 만료된 유저가 새 토큰을 받으러 오는 길목 개방!
                    "/api/products/**",      // 상품 목록과 상세 페이지는 비회원도 볼 수 있어야 합니다.
                    "/v3/api-docs/**",       // Swagger UI 관련 설정
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/uploads/**"            // 로컬에 저장된 이미지 접근
                ).permitAll()
                
                // 2. 관리자 페이지는 'ADMIN' 권한이 있는 사람만 접근 가능
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 3. 그 외의 모든 요청(장바구니 담기, 결제 등)은 반드시 로그인(인증)이 필요함!
                .anyRequest().authenticated()
            )
            // [★핵심] 기본 아이디/비밀번호 검사기가 돌기 전에, 우리가 만든 JwtFilter가 먼저 토큰을 검사하게 배치
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}