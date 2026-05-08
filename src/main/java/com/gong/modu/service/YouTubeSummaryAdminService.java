package com.gong.modu.service;


import com.gong.modu.domain.dto.AdminYouTubeSummaryReplaceRequest;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// 관리자용 YouTube 요약 데이터 교체 서비스
// EC2 서버에서 직접 YouTube 자막을 추출하지 않고 로컬에서 이미 생성된 요약 JSON을 받아 DB에 반영하는 역할 담당
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryAdminService {

    private final YouTubeVideoSummaryRepository summaryRepository;

    // 기존 youtube_video_summaries 데이터를 모두 삭제 후 요청으로 받은 새 요약 데이터로 통째로 교체
    @Transactional
    public int replaceSummaries(List<AdminYouTubeSummaryReplaceRequest> requests) {

        // 요청 body 자체가 없거나 비어 있으면 교체할 데이터가 없으므로 예외 던짐
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("교체할 YouTube 요약 데이터가 없습니다.");
        }

        // 안전장치
        if (requests.size() > 3) {
            throw new IllegalArgumentException("YouTube 요약 데이터는 최대 3개까지만 교체할 수 있습니다.");
        }

        List<YouTubeVideoSummary> newSummaries = requests.stream()
                .map(this::toEntity)
                .toList();

        // 기존 데이터를 전부 삭제
        // JPA deleteAllInBatch()는 개별 엔티티를 하나씩 조회하지 않고 bulk delete 수행
        summaryRepository.deleteAllInBatch();

        // delete SQL을 DB에 먼저 반영
        summaryRepository.flush();

        // 새 요약 데이터를 저장
        summaryRepository.saveAll(newSummaries);

        // 저장 SQL을 DB에 즉시 반영
        summaryRepository.flush();

        log.info("관리자 업로드 방식으로 YouTube 요약 데이터 교체 완료. savedCount={}",
                newSummaries.size());
        return newSummaries.size();

    }

    // 요청 DTO를 DB Entity로 변환
    private YouTubeVideoSummary toEntity(AdminYouTubeSummaryReplaceRequest request) {

        return YouTubeVideoSummary.builder()
                .channelId(request.channelId())
                .channelName(request.channelName())
                .videoId(request.videoId())
                .videoTitle(request.videoTitle())
                .videoUrl(request.videoUrl())
                .language(request.language())
                .transcriptType(request.transcriptType())
                .transcriptText(request.transcriptText())
                .summaryText(request.summaryText())
                .detailSummaryText(request.detailSummaryText())
                .thumbnailUrl(request.thumbnailUrl())
                .isRecommendedChannel(
                        request.isRecommendedChannel() != null ? request.isRecommendedChannel() : true
                )
                .collectedAt(
                        request.collectedAt() != null ? request.collectedAt() : LocalDateTime.now()
                )
                .publishedAt(request.publishedAt())
                .build();
    }

    // JSON 다운로드 메서드
    @Transactional(readOnly = true)
    public List<AdminYouTubeSummaryReplaceRequest> exportSummaries() {
        return summaryRepository.findAll().stream()
                .map(this::toReplaceRequest)
                .toList();
    }

    private AdminYouTubeSummaryReplaceRequest toReplaceRequest(YouTubeVideoSummary entity) {
        return new AdminYouTubeSummaryReplaceRequest(
                entity.getChannelId(),
                entity.getChannelName(),
                entity.getVideoId(),
                entity.getVideoTitle(),
                entity.getVideoUrl(),
                entity.getLanguage(),
                entity.getTranscriptType(),
                entity.getTranscriptText(),
                entity.getSummaryText(),
                entity.getDetailSummaryText(),
                entity.getThumbnailUrl(),
                entity.getIsRecommendedChannel(),
                entity.getCollectedAt(),
                entity.getPublishedAt()
        );
    }
}
