package com.gong.modu.domain.dto;

import java.util.List;

public record TranscriptResult(
        boolean success,
        String videoId,
        String language,
        String transcriptType,
        String text,
        List<TranscriptSegment> segments,
        String errorType,
        String message
) {
    public record TranscriptSegment(
            String text,
            Double start,
            Double duration
    ) {
    }
}
