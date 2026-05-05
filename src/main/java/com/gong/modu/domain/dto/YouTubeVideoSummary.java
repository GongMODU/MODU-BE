package com.gong.modu.domain.dto;

import java.time.OffsetDateTime;

public record YouTubeVideoSummary(
        String channelId,
        String channelTitle,
        String videoId,
        String title,
        OffsetDateTime publishedAt,
        long durationSeconds // 영상 길이를 초 단위로 저장
) {
}
