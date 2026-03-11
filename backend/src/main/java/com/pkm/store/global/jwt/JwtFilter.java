package com.pkm.store.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. 프론트엔드가 보낸 요청의 헤더에서 "Authorization" 이라는 티켓을 꺼냅니다.
        String authorizationHeader = request.getHeader("Authorization");

        // 2. 티켓이 없거나, "Bearer "로 시작하지 않으면 (규칙 위반) 그냥 통과시킵니다.
        // (가입이나 로그인은 티켓이 없어도 통과해야 하니까요. 막는 건 SecurityConfig가 알아서 합니다.)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 글자를 떼어내고 진짜 순수 토큰 문자열만 추출합니다.
        String token = authorizationHeader.substring(7);

        // 4. 토큰이 진짜인지(유효한지) 검사합니다.
        if (jwtUtil.validateToken(token)) {
            // 진짜라면 토큰 안에서 이메일과 권한(Role)을 꺼냅니다.
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 5. 스프링 시큐리티에게 "이 사람은 검증된 사람이야!" 라고 알려주기 위해 인증 객체를 만듭니다.
            // (스프링 시큐리티는 권한 이름 앞에 "ROLE_"이 붙는 걸 좋아합니다.)
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

            // 6. 스프링 시큐리티의 VIP 명부에 이 사람을 등록합니다. (이제 이 요청이 끝날 때까지 로그인된 상태로 인정됨)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 7. 다음 문지기나 목적지(Controller)로 이동시킵니다.
        filterChain.doFilter(request, response);
    }
}