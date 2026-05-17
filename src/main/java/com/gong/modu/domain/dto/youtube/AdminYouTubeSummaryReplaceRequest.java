package com.gong.modu.domain.dto.youtube;

import java.time.LocalDateTime;

// 로컬에서 생성한 YouTube 요약 데이터를 EC2 서버에 업로드할 때 사용하는 요청 DTO
// DB dump 파일 대신 JSON으로 데이터를 넘기기 위한 용도
public record AdminYouTubeSummaryReplaceRequest(
        // YouTube 채널 ID
        String channelId,
        // YouTube 채널명
        String channelName,
        // YouTube 영상 ID
        String videoId,
        // YouTube 영상 제목
        String videoTitle,
        // YouTube 영상 URL
        String videoUrl,
        // 자막 언어
        String language,
        // 자막 타입
        String transcriptType,
        // 자막 전문
        String transcriptText,
        // 홈 화면용 3줄 요약
        String summaryText,
        // 상세 모달용 긴 요약
        String detailSummaryText,
        // 썸네일 URL
        String thumbnailUrl,
        // 추천 채널 여부
        Boolean isRecommendedChannel,
        // 수집 시각
        LocalDateTime collectedAt,
        // 영상 게시 시각
        LocalDateTime publishedAt
) {
}
