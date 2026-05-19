package com.gong.modu.domain.dto.youtube;

import java.util.List;

public record VideoDetailSummaryResponse(
        String videoTitle,
        String channelName,
        String videoUrl,
        List<DetailSummarySection> sections
) {}
