package com.gong.modu.service;

import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.DailyCollectSummaryResponse;
import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.exception.CustomException;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 하루 한 번 수동으로 YouTube 자막 추출 + LLM 요약 + DB 저장을 수행하는 서비스 클래스
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyYouTubeSummaryCollectService {

    private final RandomTranscriptService randomTranscriptService;
    private final VideoSummaryBuilderService videoSummaryBuilderService;
    private final YouTubeVideoSummaryRepository summaryRepository;


    // application-local.properties의 youtube.* 설정값을 읽어오는 객체
    private final YouTubeProperties youTubeProperties;

    // 기본 목표 개수만큼 수집
    // 컨트롤러에서 targetCount를 따로 넘기지 않을 때 사용하는 메서드
    public DailyCollectSummaryResponse collectDailySummaries() {
        return collectDailySummaries(youTubeProperties.getSummaryTargetCount());
    }

    // 지정한 개수만큼 새 유튜브 요약을 수집
    // 파라미터 targetCount: 새로 저장하고 싶은 요약 개수
    public DailyCollectSummaryResponse collectDailySummaries(int targetCount) {
        // targetCount가 0 이하로 들어오면 최소 1개는 시도하도록 보정
        int normalizedTargetCount = Math.max(targetCount, 1);

        // 전체 최대 시도 횟수도 properties에서 가져옴
        // 단, 0 이하로 잘못 설정되어 있으면 최소 1번은 시도하도록 보정
        int maxAttempts = Math.max(youTubeProperties.getSummaryMaxAttempts(), 1);
        int collectedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        log.info("수동 YouTube 요약 수집 시작. targetCount={}, maxAttempts={}",
                normalizedTargetCount,
                maxAttempts);

        for (int attempt = 1; attempt <= maxAttempts && collectedCount < normalizedTargetCount; attempt++) {
            try {
                RandomTranscriptResponse transcript = randomTranscriptService.getRandomTranscript();
                if (summaryRepository.existsByVideoId(transcript.videoId())) {
                    skippedCount++;
                    log.info("이미 저장된 영상이므로 건너뜁니다. videoId={}, title={}",
                            transcript.videoId(), transcript.title());
                    continue;
                }

                videoSummaryBuilderService.buildAndSave(transcript);
                collectedCount++;
                log.info("수동 YouTube 요약 수집 성공. collectedCount={}/{}, videoId={}",
                        collectedCount, normalizedTargetCount, transcript.videoId());

            } catch (CustomException e) {
                failedCount++;
                log.warn("수동 YouTube 요약 수집 중 도메인 예외 발생. attempt={}, error={}",
                        attempt, e.getMessage());

            } catch (Exception e) {
                failedCount++;

                log.error("수동 YouTube 요약 수집 중 알 수 없는 예외 발생. attempt={}",
                        attempt, e);
            }

        }

        log.info("수동 YouTube 요약 수집 종료. targetCount={}, collected={}, skipped={}, failed={}",
                normalizedTargetCount, collectedCount, skippedCount, failedCount);

        return new DailyCollectSummaryResponse(
                normalizedTargetCount,
                collectedCount,
                skippedCount,
                failedCount
        );

    }
}
