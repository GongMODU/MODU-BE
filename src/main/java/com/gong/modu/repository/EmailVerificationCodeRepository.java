package com.gong.modu.repository;

import com.gong.modu.domain.entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    // 이메일 기준 가장 최근 인증 코드 이력 조회
    Optional<EmailVerificationCode> findTopByEmailOrderByCreatedAtDesc(String email);
}