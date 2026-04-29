package com.gong.modu.controller;

import com.gong.modu.domain.dto.InvestmentAnalysisResponse;
import com.gong.modu.domain.dto.InvestmentAnswerRequest;
import com.gong.modu.domain.dto.InvestmentQuestionResponse;
import com.gong.modu.service.InvestmentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Investment Profile", description = "개인 투자성향 분석")
@RestController
@RequestMapping("/api/investment-profile")
@RequiredArgsConstructor
public class InvestmentProfileController {

    private final InvestmentProfileService investmentProfileService;

    @Operation(summary = "질문 목록 조회")
    @GetMapping("/questions")
    public ResponseEntity<InvestmentQuestionResponse> getQuestions() {
        return ResponseEntity.ok(investmentProfileService.getQuestions());
    }

    @Operation(summary = "답변 제출 및 투자성향 분석")
    @PostMapping("/analyze")
    public ResponseEntity<InvestmentAnalysisResponse> analyze(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid InvestmentAnswerRequest request
    ) {
        return ResponseEntity.ok(investmentProfileService.analyze(userId, request));
    }
}
