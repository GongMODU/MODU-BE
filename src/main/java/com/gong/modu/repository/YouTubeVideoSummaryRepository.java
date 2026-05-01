package com.gong.modu.repository;

import com.gong.modu.domain.entity.YouTubeVideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YouTubeVideoSummaryRepository extends JpaRepository<YouTubeVideoSummary, Long> {
    Optional<YouTubeVideoSummary> findByVideoId(String videoId);
}
