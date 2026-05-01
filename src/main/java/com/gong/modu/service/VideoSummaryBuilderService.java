package com.gong.modu.service;

import com.gong.modu.constant.SummaryPrompts;
import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.domain.dto.VideoSummaryResponse;
import com.gong.modu.domain.dto.anthropic.AnthropicMessageDto;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoSummaryBuilderService {

    private final AnthropicService anthropicService;
    private final YouTubeVideoSummaryRepository summaryRepository;

    @Value("${anthropic.max-tokens.detail:4096}")
    private int detailMaxTokens;

    @Async("summaryTaskExecutor")
    @Transactional
    public CompletableFuture<VideoSummaryResponse> buildAsync(RandomTranscriptResponse transcript) {
        VideoSummaryResponse response = summaryRepository.findByVideoId(transcript.videoId())
                .map(cached -> {
                    log.info("캐시된 요약 반환. videoId={}", transcript.videoId());
                    return toSummaryResponse(cached);
                })
                .orElseGet(() -> {
                    log.info("LLM 요약 생성 시작. videoId={}", transcript.videoId());
                    String summaryText = anthropicService.call(
                            buildMessages(String.format(SummaryPrompts.SHORT_SUMMARY_TEMPLATE, transcript.transcriptText()))
                    );
                    String detailSummaryText = anthropicService.call(
                            buildMessages(String.format(SummaryPrompts.DETAIL_SUMMARY_TEMPLATE, transcript.transcriptText())),
                            detailMaxTokens
                    );

                    YouTubeVideoSummary entity = YouTubeVideoSummary.builder()
                            .channelName(transcript.channelTitle())
                            .videoId(transcript.videoId())
                            .videoTitle(transcript.title())
                            .videoUrl(transcript.videoUrl())
                            .summaryText(summaryText)
                            .detailSummaryText(detailSummaryText)
                            .isRecommendedChannel(true)
                            .build();

                    summaryRepository.save(entity);
                    log.info("요약 저장 완료. videoId={}", transcript.videoId());
                    return toSummaryResponse(entity);
                });

        return CompletableFuture.completedFuture(response);
    }

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

    private List<AnthropicMessageDto> buildMessages(String prompt) {
        return List.of(new AnthropicMessageDto("user", prompt));
    }
}
