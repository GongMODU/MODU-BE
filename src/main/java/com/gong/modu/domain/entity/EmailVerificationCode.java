package com.gong.modu.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

 //Redis에 인증코드 저장하고 -> 인증 완료 이력은 이 테이블에 기록함

@Entity
@Table(name = "email_verification_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 비밀번호 재설정 시에는 user_id 존재, 신규 회원가입 시에는 null
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // 인증 완료된 시각 (null이면 아직 미인증)
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public void verify() {
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean isVerified() {
        return this.verifiedAt != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}