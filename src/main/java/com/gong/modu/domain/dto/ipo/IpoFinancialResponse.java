package com.gong.modu.domain.dto.ipo;

import lombok.Builder;
import lombok.Getter;

// 재무 차트용 연도별 재무 하이라이트 응답 DTO
// year: bsns_year 값 그대로 반환 / 프론트에서 "제N기" 표현 처리
// 손실·자본잠식 등으로 음수 가능 → Long 타입 유지
@Getter
@Builder
public class IpoFinancialResponse {
    private String year;           // 사업연도 (예: "2023")
    private Long revenue;          // 매출액
    private Long operatingProfit;  // 영업이익 (손실 시 음수)
    private Long netIncome;        // 당기순이익 (손실 시 음수)
    private Long totalAssets;      // 자산총계
    private Long totalLiabilities; // 부채총계
    private Long totalEquity;      // 자본총계 (자본잠식 시 음수)
}
