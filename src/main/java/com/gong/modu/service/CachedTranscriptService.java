package com.gong.modu.service;

import com.gong.modu.domain.dto.TranscriptResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

// YouTube 영상 자막 추출 결과를 Redis에 캐싱하는 서비스 클래스
// 기존 TranscriptExtractor를 직접 호출하던 RandomTranscriptService가 대신 이 클래스를 직접 호출함
@Service
public class CachedTranscriptService {

    private final TranscriptExtractor transcriptExtractor;

    public CachedTranscriptService(TranscriptExtractor transcriptExtractor) {
        this.transcriptExtractor = transcriptExtractor;
    }

    // videoId를 기준으로 자막 추출 결과를 Redis에 캐싱 (TTL: 14일)
    // unless 조건: result가 null이거나 자막 추출에 실패한 결과는 캐싱 X
    @Cacheable(
            cacheNames = "youtubeTranscripts",
            key = "#videoId",
            unless = "#result == null || !#result.success()"
    )
    public TranscriptResult getTranscript(String videoId) {
        return transcriptExtractor.extract(videoId);
    }
}
