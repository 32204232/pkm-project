package com.pkm.store.domain.member.service;

import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.global.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // [추가됨]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // [추가됨] 암호화 도구 주입!
    private final JwtUtil jwtUtil;
    // 회원가입 로직
    @Transactional
    public Long join(String email, String password, String nickname, Member.Role role) {
        
        // 1. 중복 이메일 검증
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (핵심 로직 추가!)
        // 사용자가 '1234'를 입력해도 DB에는 '$2a$10$...' 같이 복잡한 문자로 저장됩니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 멤버 객체 생성 (암호화된 비밀번호를 넣습니다)
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword) 
                .nickname(nickname)
                .role(role)
                .build();

        // 4. DB에 저장
        Member savedMember = memberRepository.save(member);
        
        return savedMember.getId();
    }
    @Transactional(readOnly = true)
    public TokenResponse login(String email, String password) {
    // 1. DB에서 회원 찾기 (이메일로)
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

    // 2. 비밀번호 맞는지 확인 (자네가 PasswordEncoder를 쓰고 있다면)
    if (!passwordEncoder.matches(password, member.getPassword())) {
        throw new IllegalArgumentException("잘못된 비밀번호입니다.");
    }

    // 3. 권한(Role) 정보 빼오기 (USER 또는 ADMIN)
    String role = member.getRole().name();

    // 4. JwtUtil을 써서 토큰 만들기 (자네 JwtUtil 보니까 이름이 generateToken 이더군!)
    String accessToken = jwtUtil.generateToken(member.getEmail(), role);

    // 5. 토큰이랑 권한을 TokenResponse 박스에 예쁘게 담아서 컨트롤러에게 전달!
    return new TokenResponse(accessToken, role);
}

    
}