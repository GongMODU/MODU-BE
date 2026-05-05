package com.gong.modu.service;

import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.DailyCollectSummaryResponse;
import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // 새 요약을 목표 개수만큼 먼저 메모리에 모은 다음, 목표 개수를 채웠을 때만 기존 데이터를 삭제하고 새 데이터를 저장함
    public DailyCollectSummaryResponse collectDailySummaries(int targetCount) {

        int normalizedTargetCount = Math.max(targetCount, 1);
        int maxAttempts = Math.max(youTubeProperties.getSummaryMaxAttempts(), 1);

        int collectedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        // 이번 수집 API 호출에서 새로 저장할 요약 엔티티들을 임시로 담아두는 리스트
        List<YouTubeVideoSummary> newSummaries = new ArrayList<>();

        // 이번 수집 과정 안에서 같은 videoId가 중복으로 뽑히는 것을 막기 위한 Set
        Set<String> collectedVideoIds = new HashSet<>();

        log.info("수동 YouTube 요약 수집 시작. targetCount={}, maxAttempts={}",
                normalizedTargetCount,
                maxAttempts);

        for (int attempt = 1; attempt <= maxAttempts && collectedCount < normalizedTargetCount; attempt++) {
            try {
                RandomTranscriptResponse transcript = randomTranscriptService.getRandomTranscript();


                if (collectedVideoIds.contains(transcript.videoId())) {
                    skippedCount++;
                    log.info("이미 저장된 영상이므로 건너뜁니다. videoId={}, title={}",
                            transcript.videoId(), transcript.title());
                    continue;
                }

                // 여기서는 DB에 저장하지 않고, LLM 요약을 생성한 엔티티만 받아옴
                YouTubeVideoSummary summary = videoSummaryBuilderService.build(transcript);

                newSummaries.add(summary);
                collectedVideoIds.add(transcript.videoId());
                collectedCount++;

                log.info("수동 YouTube 요약 수집 성공. collectedCount={}/{}, videoId={}",
                        collectedCount, normalizedTargetCount, transcript.videoId());

                if (collectedCount < normalizedTargetCount) {
                    sleepBeforeNextLlmRequest();
                }

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

        // 목표 개수를 다 못 채웠다면 기존 DB는 그대로 유지
        if (collectedCount < normalizedTargetCount) {
            log.warn("목표 요약 개수를 채우지 못해 기존 YouTube 요약 데이터를 유지합니다. targetCount={}, collected={}",
                    normalizedTargetCount, collectedCount);

            return new DailyCollectSummaryResponse(
                    normalizedTargetCount,
                    collectedCount,
                    skippedCount,
                    failedCount
            );
        }

        // 여기까지 왔다는 것은 목표 개수만큼 새 요약 생성에 성공했다는 뜻이므로
        // 이제 기존 데이터를 삭제하고 새 데이터로 교체
        summaryRepository.deleteAllInBatch();
        summaryRepository.flush();
        summaryRepository.saveAll(newSummaries);
        summaryRepository.flush();

        log.info("기존 YouTube 요약 데이터를 새 데이터로 교체 완료. savedCount={}",
                newSummaries.size());
        log.info("수동 YouTube 요약 수집 종료. targetCount={}, collected={}, skipped={}, failed={}",
                normalizedTargetCount, collectedCount, skippedCount, failedCount);

        return new DailyCollectSummaryResponse(
                normalizedTargetCount,
                collectedCount,
                skippedCount,
                failedCount
        );
    }

    private void sleepBeforeNextLlmRequest() {
        try {
            Thread.sleep(60_000); // 60초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
