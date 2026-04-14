package com.gong.modu.controller;

import com.gong.modu.domain.dto.*;
import com.gong.modu.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 이메일 인증코드 발송
    @PostMapping("/email/sendcode")
    public ResponseEntity<Void> sendEmailCode(@RequestBody @Valid EmailVerificationRequest request) {
        authService.sendEmailVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증코드 검증
    @PostMapping("/email/verifycode")
    public ResponseEntity<Void> verifyEmailCode(@RequestBody @Valid EmailCodeVerifyRequest request) {
        authService.verifyEmailCode(request);
        return ResponseEntity.ok().build();
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid LocalSignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LocalLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // 로그아웃 (인증 필요)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal Long userId
    ) {
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken, userId);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 재설정 링크 발송
    @PostMapping("/password/resetrequest")
    public ResponseEntity<Void> sendPasswordResetLink(@RequestBody @Valid PasswordResetRequest request) {
        authService.sendPasswordResetLink(request);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 재설정
    @PostMapping("/password/resetconfirm")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetConfirmRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
