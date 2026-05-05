package com.gong.modu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gong.modu.constant.SummaryPrompts;
import com.gong.modu.domain.dto.LlmVideoSummaryResult;
import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.domain.dto.VideoSummaryResponse;
import com.gong.modu.domain.dto.anthropic.AnthropicMessageDto;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
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

// YouTube 자막을 LLM으로 요약하고 YouTubeVideoSummary 엔티티로 변환하는 서비스 클래스
// 기존: 사용자 요청 시점에 요약을 생성 -> 변경: 하루 한 번 수동 수집 작업에 사용
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoSummaryBuilderService {

    private final AnthropicService anthropicService;
    private final YouTubeVideoSummaryRepository summaryRepository;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.max-tokens.detail:4096}")
    private int detailMaxTokens;

    // 자막 데이터를 바탕으로 LLM 요약을 생성하고, YouTubeVideoSummary 엔티티를 만들어 반환하는 메서드
    public YouTubeVideoSummary build(RandomTranscriptResponse transcript) {

        log.info("LLM 요약 생성 시작. videoId={}, title={}",
                transcript.videoId(),
                transcript.title());

        // Claude에 원본 자막 전체를 그대로 전달함
        String transcriptText = normalizeTranscriptText(transcript.transcriptText());

        log.info("Claude 전달 자막 준비 완료. videoId={}, transcriptLength={}",
                transcript.videoId(),
                transcriptText.length());

        // 짧은 요약 + 상세 요약을 한 번의 Claude 호출로 생성함
        LlmVideoSummaryResult summaryResult = createSummaryWithSingleLlmCall(transcriptText);

        // shortSummaryLines를 DB의 summaryText 컬럼에 저장하기 위해 줄바꿈 문자열로 변환
        String summaryText = String.join("\n", summaryResult.shortSummaryLines());


//        // 홈 화면용 짧은 3줄 요약 생성
//        String summaryText = anthropicService.call(
//                buildMessages(String.format(
//                        SummaryPrompts.SHORT_SUMMARY_TEMPLATE,
//                        transcript.transcriptText()
//                ))
//        );
//        // 짧은 요약 요청 직후 바로 긴 요약 요청을 보내면 input token per minute 제한에 걸릴 수 있으므로 잠시 대기
//        sleepBeforeNextLlmRequest();
//
//        // 상세 모달용 긴 요약 생성
//        String detailSummaryText = anthropicService.call(
//                buildMessages(String.format(
//                        SummaryPrompts.DETAIL_SUMMARY_TEMPLATE,
//                        transcript.transcriptText()
//                )),
//                detailMaxTokens
//        );

        // LLM 요약 결과와 자막 원문을 DB Entity로 변환
        // 단, 여기서는 save() 하지 않고 엔티티만 만들어서 반환함
        return YouTubeVideoSummary.builder()
                .channelId(transcript.channelId())
                .channelName(transcript.channelTitle())
                .videoId(transcript.videoId())
                .videoTitle(transcript.title())
                .videoUrl(transcript.videoUrl())
                .language(transcript.language())
                .transcriptType(transcript.transcriptType())

                .transcriptText(transcriptText)
                .summaryText(summaryText)
                .detailSummaryText(summaryResult.detailSummaryText())
                .thumbnailUrl(null)
                .isRecommendedChannel(true)
                .collectedAt(LocalDateTime.now())
                .publishedAt(toLocalDateTime(transcript.publishedAt()))
                .build();
    }

    // 하루 한 번 수동 수집에서 사용할 동기 요약 생성 메서드
    // 기존 방식처럼 LLM 요약 생성 후 바로 DB에 저장하는 메서드
    @Transactional
    public VideoSummaryResponse buildAndSave(RandomTranscriptResponse transcript) {
        return summaryRepository.findByVideoId(transcript.videoId())
                .map(cached -> {
                    log.info("이미 저장된 YouTube 요약 재사용. videoId={}", transcript.videoId());
                    return toSummaryResponse(cached);
                })
                .orElseGet(() -> {
                    YouTubeVideoSummary entity = build(transcript);
                    YouTubeVideoSummary saved = summaryRepository.save(entity);

                    log.info("YouTube 요약 DB 저장 완료. videoId={}, id={}",
                            saved.getVideoId(),
                            saved.getId());

                    return toSummaryResponse(saved);
                });
    }

    // Claude API를 한 번만 호출해서 3줄 요약과 상세 요약을 같이 생성하는 메서드
    private LlmVideoSummaryResult createSummaryWithSingleLlmCall(String transcriptText) {
        String prompt = String.format(
                SummaryPrompts.YOUTUBE_SUMMARY_JSON_TEMPLATE,
                transcriptText
        );

        String llmResponse = anthropicService.call(
                buildMessages(prompt),
                detailMaxTokens
        );

        try {
            String json = extractJson(llmResponse);
            LlmVideoSummaryResult result = objectMapper.readValue(
                    json,
                    LlmVideoSummaryResult.class
            );

            validateSummaryResult(result);

            return result;

        } catch (Exception e) {
            log.error("Claude 요약 JSON 파싱 실패. response={}", llmResponse, e);

            throw new CustomException(ErrorCode.CLAUDE_API_ERROR);
        }
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

    private void sleepBeforeNextLlmRequest() {
        try {
            Thread.sleep(20_000); // 20초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Claude가 혹시 앞뒤에 불필요한 문장을 붙였을 때를 대비해 JSON 부분만 잘라내는 메서드
    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Claude response is blank");
        }

        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");

        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new IllegalArgumentException("JSON object not found in Claude response");
        }

        return response.substring(startIndex, endIndex + 1);
    }

    // Claude 응답이 서비스에서 사용할 수 있는 형태인지 검증하는 메서드
    private void validateSummaryResult(LlmVideoSummaryResult result) {

        if (result == null) {
            throw new IllegalArgumentException("summary result is null");
        }

        if (result.shortSummaryLines() == null || result.shortSummaryLines().size() != 3) {
            throw new IllegalArgumentException("shortSummaryLines must contain exactly 3 lines");
        }

        if (result.detailSummaryText() == null || result.detailSummaryText().isBlank()) {
            throw new IllegalArgumentException("detailSummaryText is blank");
        }
    }

    // 자막 문자열을 Claude와 DB 저장에 사용할 수 있도록 정리하는 메서드
    // 최대 글자 수 제한은 하지 않고, null/blank 방어와 앞뒤 공백 제거만 수행함
    private String normalizeTranscriptText(String transcriptText) {
        if (transcriptText == null || transcriptText.isBlank()) {
            return "";
        }

        return transcriptText.strip();
    }
}
