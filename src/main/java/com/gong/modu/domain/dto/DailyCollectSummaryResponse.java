package com.gong.modu.domain.dto;

// 하루 1번 수동 유튜브 요약 수집 작업의 결과를 반환하는 DTO
public record DailyCollectSummaryResponse(
        int targetCount,
        int collectedCount,
        int skippedCount,
        int failedCount
) {
}
