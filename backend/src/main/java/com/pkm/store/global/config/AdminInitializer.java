package com.pkm.store.global.config;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // "admin@store.com" 계정이 DB에 없는 경우에만 생성한다!
        if (memberRepository.findByEmail("admin@store.com").isEmpty()) {
            Member admin = Member.builder()
                    .email("admin@store.com")
                    // [주의] 비밀번호는 반드시 암호화해서 넣어야 로그인할 때 에러가 안 난다!
                    .password(passwordEncoder.encode("admin1234")) 
                    .nickname("포켓몬스토어 최고점장")
                    .role(Member.Role.ADMIN) // 드디어 ADMIN 부여!
                    .build();

            memberRepository.save(admin);
            System.out.println("⚡ [시스템] 임시 관리자 계정이 생성되었습니다! (admin@store.com / admin1234) 삐까! ⚡");
        }
    }
}