package com.gong.modu.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// 유튜브 영상 요약 정보를 저장하는 엔티티
// 변경 -> 하루 한 번 수동 수집을 통해 생성된 유튜브 요약 컨텐츠 저장소 역할
@Entity
@Table(
        name = "youtube_video_summaries",
        indexes = {
                @Index(name = "idx_youtube_video_summaries_video_id", columnList = "video_id"),
                @Index(name = "idx_youtube_video_summaries_created_at", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class YouTubeVideoSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유튜브 채널 ID
    @Column(name = "channel_id")
    private String channelId;

    // 채널명
    @Column(name = "channel_name", nullable = false)
    private String channelName;

    @Column(name = "video_id", nullable = false, unique = true)
    private String videoId;

    @Column(name = "video_title", nullable = false)
    private String videoTitle;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    // 추출된 자막 언어 (ko / en)
    @Column(name = "language")
    private String language;

    // 자막 종류 (manual / generated)
    @Column(name = "transcript_type")
    private String transcriptType;

    // 자막 원문
    @Column(name = "transcript_text", columnDefinition = "TEXT")
    private String transcriptText;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "detail_summary_text", columnDefinition = "TEXT")
    private String detailSummaryText;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_recommended_channel")
    private Boolean isRecommendedChannel;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
