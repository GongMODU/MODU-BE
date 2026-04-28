package com.gong.modu.repository;

import com.gong.modu.domain.entity.User;
import com.gong.modu.domain.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//이메일, 소셜 제공자+소셜ID 기준 조회

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 소셜 제공자 + 소셜 고유 ID로 사용자 조회 (소셜 로그인 식별용)
    Optional<User> findByProviderAndProviderUserId(Provider provider, String providerUserId);

    // 중복 여부 확인
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}