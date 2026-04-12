package com.gong.modu.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    private static final String EMAIL_CODE_PREFIX = "email:code:";
    private static final String EMAIL_VERIFIED_PREFIX = "email:verified:";

    // 이메일 인증코드 저장 (TTL: 초 단위)
    public void saveEmailCode(String email, String code, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(EMAIL_CODE_PREFIX + email, code, Duration.ofSeconds(ttlSeconds));
    }

    // 이메일 인증코드 조회
    public Optional<String> getEmailCode(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(EMAIL_CODE_PREFIX + email));
    }

    // 이메일 인증코드 삭제 (인증 완료 후 즉시 제거)
    public void deleteEmailCode(String email) {
        redisTemplate.delete(EMAIL_CODE_PREFIX + email);
    }

    // 이메일 인증 완료 마킹 저장
    public void markEmailVerified(String email) {
        redisTemplate.opsForValue()
                .set(EMAIL_VERIFIED_PREFIX + email, "true", Duration.ofMinutes(10));
    }

    // 이메일 인증 완료 여부 확인
    public boolean isEmailVerified(String email) {
        return Boolean.TRUE.toString().equals(
                redisTemplate.opsForValue().get(EMAIL_VERIFIED_PREFIX + email)
        );
    }

    // 이메일 인증 완료 마킹 삭제 (회원가입 완료 후)
    public void deleteEmailVerified(String email) {
        redisTemplate.delete(EMAIL_VERIFIED_PREFIX + email);
    }

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    // 리프레시 토큰 저장
    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_PREFIX + userId, refreshToken, Duration.ofMillis(ttlMillis));
    }

    // 리프레시 토큰 조회
    public Optional<String> getRefreshToken(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId));
    }

    // 리프레시 토큰 삭제 (로그아웃)
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    private static final String BLACKLIST_PREFIX = "blacklist:";

    // 액세스 토큰 블랙리스트 등록
    public void addToBlacklist(String accessToken, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + accessToken, "logout", Duration.ofMillis(ttlMillis));
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }
}