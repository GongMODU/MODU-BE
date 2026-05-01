package com.gong.modu.domain.dto;

import java.util.List;

public record VideoSummaryResponse(
        String videoId,
        String videoTitle,
        String channelName,
        String videoUrl,
        List<String> summaryLines
) {}
