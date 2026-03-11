package com.pkm.store.domain.member.repository;

import com.pkm.store.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
    // 이메일로 회원을 찾는 기능 (로그인이나 중복 가입 방지 시 사용)
    Optional<Member> findByEmail(String email);
    
    // 이메일이 이미 존재하는지 확인하는 기능
    boolean existsByEmail(String email);
}