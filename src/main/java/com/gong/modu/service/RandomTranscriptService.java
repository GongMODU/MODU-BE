package com.gong.modu.service;

import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.domain.dto.TranscriptResult;
import com.gong.modu.domain.dto.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// 랜덤 영상 선택 + 자막 추출을 하나의 유스케이스로 묶는 서비스 클래스
@Slf4j
@Service
public class RandomTranscriptService {

    private final YouTubeVideoService youTubeVideoService; // 최신 영상 중 랜덤 영상 선택하는 클래스
    private final TranscriptExtractor transcriptExtractor; //  Python 스크립트를 실행해서 자막 가져오는 클래스
    private final YouTubeProperties properties; // YouTube 관련 설정값을 application.properties에서 읽어오는 클래스

    public RandomTranscriptService(
            YouTubeVideoService youTubeVideoService,
            TranscriptExtractor transcriptExtractor,
            YouTubeProperties properties
    ) {
        this.youTubeVideoService = youTubeVideoService;
        this.transcriptExtractor = transcriptExtractor;
        this.properties = properties;
    }

    // 여러 채널의 최신 영상 중 자막이 존재하는 랜덤 영상을 하나 반환
    // LLM 요약 서비스에서 이 메서드를 호출한 뒤 반환된 RandomTranscriptResponse의 transcriptText()를 LLM에 넘기면 됨
    public RandomTranscriptResponse getRandomTranscript() {
        List<YouTubeVideoSummary> shuffledPool = youTubeVideoService.collectShuffledLatestVideoPool();

        if (shuffledPool.isEmpty()) {
            log.warn("YouTube 영상 pool이 비어 있습니다.");
            throw new CustomException(ErrorCode.VIDEO_POOL_EMPTY);
        }

        int retryLimit = Math.min(properties.getTranscriptRetryCount(), shuffledPool.size());

        log.info("랜덤 YouTube 자막 추출 시작. poolSize={}, retryLimit={}",
                shuffledPool.size(),
                retryLimit
        );

        for (int i = 0; i < retryLimit; i++) {
            YouTubeVideoSummary video = shuffledPool.get(i);

            log.info("자막 추출 시도. attempt={}/{},channelTitle={}, videoId={}, title={}",
                    i + 1,
                    retryLimit,
                    video.channelTitle(),
                    video.videoId(),
                    video.title()
            );

            TranscriptResult transcript = transcriptExtractor.extract(video.videoId());

            if (isValidTranscript(transcript)) {
                log.info("자막 추출 성공. channelTitle={}, videoId={}, language={}, transcriptType={}",
                        video.channelTitle(),
                        video.videoId(),
                        transcript.language(),
                        transcript.transcriptType()
                );

                return new RandomTranscriptResponse(

                        video.videoId(),
                        "https://www.youtube.com/watch?v=" + video.videoId(),
                        video.title(),
                        video.channelId(),
                        video.channelTitle(),
                        transcript.language(),
                        transcript.transcriptType(),
                        transcript.text()
                );
            }

            log.warn("자막 추출 실패. videoId={}, errorType={}, message={}",
                    video.videoId(),
                    transcript != null ? transcript.errorType() : "NULL_RESPONSE",
                    transcript != null ? transcript.message() : "TranscriptExtractor returned null"
            );
        }

        log.warn("재시도 횟수 내에 자막이 있는 영상을 찾지 못했습니다. retryLimit={}", retryLimit);

        throw new CustomException(ErrorCode.TRANSCRIPT_NOT_FOUND);

    }

    // LLM 요약으로 넘길만한 유효한 자막인지 검사
    // success가 true여도 text가 비어있으면 요약에 사용할 수 없으므로 실패로 봄
    private boolean isValidTranscript(TranscriptResult transcript) {
        return transcript != null
                && transcript.success()
                && transcript.text() != null
                && !transcript.text().isBlank();
    }
}
