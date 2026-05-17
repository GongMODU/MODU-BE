package com.gong.modu.domain.dto.youtube;

public record VideoDetailSummaryResponse(
        String videoTitle,
        String channelName,
        String videoUrl,
        String detailSummary
) {}
