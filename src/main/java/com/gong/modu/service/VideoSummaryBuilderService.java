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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// YouTube 자막을 LLM으로 요약하고 DB에 저장하는 서비스 클래스
// 기존: 사용자 요청 시점에 요약을 생성 -> 변경: 하루 한 번 수동 수집 작업에 사용
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoSummaryBuilderService {

    private final AnthropicService anthropicService;
    private final YouTubeVideoSummaryRepository summaryRepository;

    @Value("${anthropic.max-tokens.detail:4096}")
    private int detailMaxTokens;

    // 하루 한 번 수동 수집에서 사용할 동기 요약 생성 메서드
    @Transactional
    public VideoSummaryResponse buildAndSave(RandomTranscriptResponse transcript) {
        return summaryRepository.findByVideoId(transcript.videoId())
                .map(cached -> {
                    log.info("이미 저장된 YouTube 요약 재사용. videoId={}", transcript.videoId());
                    return toSummaryResponse(cached);
                })
                .orElseGet(() -> createSummaryAndSave(transcript));
    }

    // 기존 비동기 방식이 필요한 곳이 있을 수 있으므로 유지
    // 다만 사용자 API는 DB에 저장된 요약만 조회해야 안정적이므로 더이상 이 메서드를 직접 호출하지 않아야 함
    @Async("summaryTaskExecutor")
    @Transactional
    public CompletableFuture<VideoSummaryResponse> buildAsync(RandomTranscriptResponse transcript) {
        VideoSummaryResponse response = buildAndSave(transcript);
        return CompletableFuture.completedFuture(response);
    }
//    @Async("summaryTaskExecutor")
//    @Transactional
//    public CompletableFuture<VideoSummaryResponse> buildAsync(RandomTranscriptResponse transcript) {
//        VideoSummaryResponse response = summaryRepository.findByVideoId(transcript.videoId())
//                .map(cached -> {
//                    log.info("캐시된 요약 반환. videoId={}", transcript.videoId());
//                    return toSummaryResponse(cached);
//                })
//                .orElseGet(() -> {
//                    log.info("LLM 요약 생성 시작. videoId={}", transcript.videoId());
//                    String summaryText = anthropicService.call(
//                            buildMessages(String.format(SummaryPrompts.SHORT_SUMMARY_TEMPLATE, transcript.transcriptText()))
//                    );
//                    String detailSummaryText = anthropicService.call(
//                            buildMessages(String.format(SummaryPrompts.DETAIL_SUMMARY_TEMPLATE, transcript.transcriptText())),
//                            detailMaxTokens
//                    );
//
//                    YouTubeVideoSummary entity = YouTubeVideoSummary.builder()
//                            .channelName(transcript.channelTitle())
//                            .videoId(transcript.videoId())
//                            .videoTitle(transcript.title())
//                            .videoUrl(transcript.videoUrl())
//                            .summaryText(summaryText)
//                            .detailSummaryText(detailSummaryText)
//                            .isRecommendedChannel(true)
//                            .build();
//
//                    summaryRepository.save(entity);
//                    log.info("요약 저장 완료. videoId={}", transcript.videoId());
//                    return toSummaryResponse(entity);
//                });
//
//        return CompletableFuture.completedFuture(response);
//    }

    // 실제 LLM 호출과 DB 저장 수행
    private VideoSummaryResponse createSummaryAndSave(RandomTranscriptResponse transcript) {
        log.info("LLM 요약 생성 시작. videoId={}, title={}",
                transcript.videoId(),
                transcript.title());

        String summaryText = anthropicService.call(
                buildMessages(String.format(
                        SummaryPrompts.SHORT_SUMMARY_TEMPLATE,
                        transcript.transcriptText()
                ))
        );

        String detailSummaryText = anthropicService.call(
                buildMessages(String.format(
                        SummaryPrompts.DETAIL_SUMMARY_TEMPLATE,
                        transcript.transcriptText()
                )),
                detailMaxTokens
        );

        // LLM 요약 결과와 자막 원문을 DB Entity로 변환
        YouTubeVideoSummary entity = YouTubeVideoSummary.builder()
                .channelId(transcript.channelId())
                .channelName(transcript.channelTitle())
                .videoId(transcript.videoId())
                .videoTitle(transcript.title())
                .videoUrl(transcript.videoUrl())
                .language(transcript.language())
                .transcriptType(transcript.transcriptType())
                .transcriptText(transcript.transcriptText())
                .summaryText(summaryText)
                .detailSummaryText(detailSummaryText)
                .thumbnailUrl(null)
                .isRecommendedChannel(true)
                .collectedAt(LocalDateTime.now())
                .publishedAt(toLocalDateTime(transcript.publishedAt()))
                .build();

        YouTubeVideoSummary saved = summaryRepository.save(entity);

        log.info("YouTube 요약 DB 저장 완료. videoId={}. id={}",
                saved.getVideoId(),
                saved.getId());

        return toSummaryResponse(saved);
    }

    // 엔티티를 홈 화면용 3줄 요약 응답 DTO로 변환
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

    // 유튜브 API에서 받은 OffsetDateTime을 DB 저장용 LocalDateTime으로 변환하는 메서드
    private LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        return offsetDateTime.toLocalDateTime();
    }
}
