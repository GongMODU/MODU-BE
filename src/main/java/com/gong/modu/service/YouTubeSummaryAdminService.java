package com.gong.modu.service;


import com.gong.modu.domain.dto.AdminYouTubeSummaryReplaceRequest;
import com.gong.modu.domain.dto.AdminYouTubeSummaryReplaceResponse;
import com.gong.modu.domain.entity.YouTubeVideoSummary;
import com.gong.modu.repository.YouTubeVideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// 관리자용 YouTube 요약 요약 관리자 기능을 담당하는 서비스
// EC2 서버에서 직접 YouTube 자막을 추출하지 않고 로컬에서 이미 생성된 요약 JSON을 받아 DB에 반영하는 역할 담당
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryAdminService {

    private final YouTubeVideoSummaryRepository summaryRepository;

    // 운영 서버에서 받은 요약 데이터로 기존 youtube_video_summaries 데이터를 전부 교체
    @Transactional
    public AdminYouTubeSummaryReplaceResponse replaceSummaries(List<AdminYouTubeSummaryReplaceRequest> requests) {

        summaryRepository.deleteAllInBatch();
        summaryRepository.flush();

        List<YouTubeVideoSummary> entities = requests.stream()
                .map(this::toEntity)
                .toList();

        summaryRepository.saveAll(entities);
        summaryRepository.flush();

        return new AdminYouTubeSummaryReplaceResponse(
                entities.size()
        );
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

    // 로컬 DB에 저장된 YouTube 요약 데이터를 export용 DTO 리스트로 변환
    @Transactional(readOnly = true)
    public List<AdminYouTubeSummaryReplaceRequest> buildReplaceRequests() {
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
