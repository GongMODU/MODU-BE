package com.gong.modu.domain.dto;

import java.util.List;

// Claude가 한 번의 응답으로 내려주는 유튜브 요약 결과 DTO
// shortSummaryLines: 홈 화면에서 보여줄 3줄 요약
// detailSummaryText: 상세 모달에서 보여줄 긴 요약
public record LlmVideoSummaryResult(
        List<String> shortSummaryLines,
        String detailSummaryText
) {
}
