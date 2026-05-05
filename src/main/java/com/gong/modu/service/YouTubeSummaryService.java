package com.gong.modu.service;

import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.domain.dto.VideoDetailSummaryResponse;
import com.gong.modu.domain.dto.VideoSummaryResponse;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// 사용자에게 YouTube 요약 콘텐츠를 제공하는 서비스 클래스
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryService {

    private static final int SUMMARY_COUNT = 3;

    private final YouTubeVideoSummaryRepository summaryRepository;

    // 홈 화면용 3줄 요약 목록을 반환
    // 기존: 랜덤 자막 추출, LLM 요약, 결과 반환
    // 변경: DB에 저장된 요약 중 랜덤 3개 조회 & 결과 반환
    @Transactional(readOnly = true)
    public List<VideoSummaryResponse> getSummaries() {
        List<YouTubeVideoSummary> summaries = summaryRepository.findRandomSummaries(SUMMARY_COUNT);

        if (summaries.isEmpty()) {
            throw new CustomException(ErrorCode.SUMMARY_NOT_FOUND);
        }

        return summaries.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    // 상세 모달용 긴 설명을 반환
    @Transactional(readOnly = true)
    public VideoDetailSummaryResponse getDetailSummary(String videoId) {
        YouTubeVideoSummary summary = summaryRepository.findByVideoId(videoId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUMMARY_NOT_FOUND));

        return new VideoDetailSummaryResponse(
                summary.getVideoTitle(),
                summary.getChannelName(),
                summary.getVideoUrl(),
                summary.getDetailSummaryText()
        );
    }

    // 엔티티를 홈 화면용 응답 DTO로 변환하는 메서드
    private VideoSummaryResponse toSummaryResponse(YouTubeVideoSummary entity) {
        List<String> lines = Arrays.stream(entity.getSummaryText().split("\n"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .limit(3)
                .toList();

        return new VideoSummaryResponse(
                entity.getVideoId(),
                entity.getVideoTitle(),
                entity.getChannelName(),
                entity.getVideoUrl(),
                lines
        );
    }

}
