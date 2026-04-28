package com.gong.modu.controller;

import com.gong.modu.domain.dto.*;
import com.gong.modu.service.AuthService;
import com.gong.modu.service.InvestmentProfileService;
import com.gong.modu.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final AuthService authService;
    private final InvestmentProfileService investmentProfileService;

    @Operation(summary = "마이페이지 홈 조회")
    @GetMapping("/home")
    public ResponseEntity<MyPageHomeResponse> getHome(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(myPageService.getHome(userId));
    }

    @Operation(summary = "닉네임 변경")
    @PatchMapping("/profile/nickname")
    public ResponseEntity<Void> updateNickname(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid NicknameUpdateRequest request
    ) {
        myPageService.updateNickname(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정")
    @PatchMapping("/profile/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PasswordUpdateRequest request
    ) {
        myPageService.updatePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "재분석용 질문 목록 조회")
    @GetMapping("/investment-profile/questions")
    public ResponseEntity<InvestmentQuestionResponse> getQuestions() {
        return ResponseEntity.ok(investmentProfileService.getQuestions());
    }

    @Operation(summary = "투자성향 재분석 제출")
    @PostMapping("/investment-profile/reanalyze")
    public ResponseEntity<InvestmentAnalysisResponse> reanalyze(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid InvestmentAnswerRequest request
    ) {
        return ResponseEntity.ok(investmentProfileService.analyze(userId, request));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal Long userId
    ) {
        authService.logout(authHeader.substring(7), userId);
        return ResponseEntity.ok().build();
    }
}
