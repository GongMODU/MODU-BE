package com.gong.modu.domain.dto;

public record VideoDetailSummaryResponse(
        String videoTitle,
        String channelName,
        String videoUrl,
        String detailSummary
) {}
