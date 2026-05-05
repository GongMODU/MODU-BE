package com.gong.modu.domain.dto;

import java.time.OffsetDateTime;

// 랜덤 YouTube 영상의 자막 추출 결과를 담는 최종 응답 DTO
// 이 DTO는 검증용 컨트롤러와 LLM 요약 서비스 입력 데이터로 쓰이면 됨
public record RandomTranscriptResponse(
        String videoId,
        String videoUrl,
        String title,
        String channelId,
        String channelTitle,
        OffsetDateTime publishedAt,
        String language,
        String transcriptType,
        String transcriptText
) {
}
