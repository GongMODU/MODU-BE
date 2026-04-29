package com.gong.modu.domain.dto;

import java.time.OffsetDateTime;

public record YouTubeVideoSummary(
        String channelId,
        String channelTitle,
        String videoId,
        String title,
        OffsetDateTime publishedAt
) {
}
