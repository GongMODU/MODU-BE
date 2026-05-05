package com.gong.modu.repository;

import com.gong.modu.domain.entity.YouTubeVideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface YouTubeVideoSummaryRepository extends JpaRepository<YouTubeVideoSummary, Long> {
    // videoId로 이미 저장된 영상인지 확인할 때 사용
    Optional<YouTubeVideoSummary> findByVideoId(String videoId);

    // 같은 영상 중복 저장을 막기 위해 사용
    boolean existsByVideoId(String videoId);

    // PostgreSQL에서 랜덤으로 요약 데이터를 가져옴
    @Query(
            value = """
                    SELECT *
                    FROM youtube_video_summaries
                    WHERE summary_text IS NOT NULL
                      AND detail_summary_text IS NOT NULL
                    ORDER BY random()
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<YouTubeVideoSummary> findRandomSummaries(int limit);
}
