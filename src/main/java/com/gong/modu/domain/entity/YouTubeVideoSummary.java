package com.gong.modu.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "youtube_video_summaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class YouTubeVideoSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
