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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryService {

    private static final int SUMMARY_COUNT = 3;
    private static final int MAX_ATTEMPTS = 9;

    private final RandomTranscriptService randomTranscriptService;
    private final VideoSummaryBuilderService videoSummaryBuilderService;
    private final YouTubeVideoSummaryRepository summaryRepository;

    public List<VideoSummaryResponse> getSummaries() {
        List<RandomTranscriptResponse> transcripts = collectTranscripts();

        // 영상 3개를 동시에 던짐
        List<CompletableFuture<VideoSummaryResponse>> futures = transcripts.stream()
                .map(videoSummaryBuilderService::buildAsync)
                .toList();

        return futures.stream()
                .map(CompletableFuture::join) // 백그라운드 스레드에서 실행 -> 결과 안 기다리고 바로 다음으로 넘어감
                .toList();
    }

    private List<RandomTranscriptResponse> collectTranscripts() {
        List<RandomTranscriptResponse> transcripts = new ArrayList<>();
        Set<String> seenVideoIds = new HashSet<>();

        for (int i = 0; i < MAX_ATTEMPTS && transcripts.size() < SUMMARY_COUNT; i++) {
            RandomTranscriptResponse transcript;
            try {
                transcript = randomTranscriptService.getRandomTranscript();
            } catch (CustomException e) {
                log.warn("자막 추출 실패로 수집 중단. 수집된 영상 수={}", transcripts.size());
                break;
            }

            if (seenVideoIds.contains(transcript.videoId())) continue;
            seenVideoIds.add(transcript.videoId());
            transcripts.add(transcript);
        }

        return transcripts;
    }

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

}
