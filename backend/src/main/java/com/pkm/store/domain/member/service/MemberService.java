package com.pkm.store.domain.member.service;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder; // [추가됨]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(String email, String password, String nickname, Member.Role role) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(role)
                .build();

        return memberRepository.save(member).getId();
    }
    // login 메서드는 AuthService로 이동했으므로 여기서 삭제합니다.
}