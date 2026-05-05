package com.gong.modu.service;

import com.gong.modu.client.YouTubeClient;
import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 여러 YouTube 채널에서 최신 영상 목록을 수집하고, 그 중 랜덤 영상을 선택하는 서비스 클래스
@Slf4j
@Service
public class YouTubeVideoService {

    private final YouTubeClient youTubeClient;
    private final YouTubeProperties properties;
    private final SecureRandom random = new SecureRandom();

    public YouTubeVideoService(YouTubeClient youTubeClient, YouTubeProperties properties) {
        this.youTubeClient = youTubeClient;
        this.properties = properties;
    }

    // application.properties에 등록된 모든 채널에서 최신 영상을 가져와 하나의 pool로 합침
    // Redis에 캐싱하는 부분 추가 (TTL: 30분)
    @Cacheable(
            cacheNames = "youtubeLatestVideos",
            key = "'pool'"
    )
    public List<YouTubeVideoSummary> collectLatestVideoPool() {
        List<YouTubeVideoSummary> pool = new ArrayList<>();

        long minDurationSeconds = Math.max(properties.getMinVideoDurationSeconds(), 0);

        for (String channelId : properties.getChannelIds()) {
            List<YouTubeVideoSummary> videos = youTubeClient.getLatestVideos(
                    channelId,
                    properties.getLatestVideoCountPerChannel()
            );
            List<YouTubeVideoSummary> filteredVideos = videos.stream()
                    // durationSeconds가 설정값보다 짧으면 숏폼 영상으로 판단하고 제외함
                    .filter(video -> video.durationSeconds() >= minDurationSeconds)
                    .toList();

            log.info(
                    "YouTube 최신 영상 필터링 완료. channelId={}, originalCount={}, filteredCount={}, minDurationSeconds={}",
                    channelId,
                    videos.size(),
                    filteredVideos.size(),
                    minDurationSeconds
            );

            pool.addAll(filteredVideos); // 롱폼으로 필터링 된 영상들만 pool에 추가
        }

        return pool;
    }

    // 영상 pool에서 랜덤으로 하나를 선택
    public YouTubeVideoSummary pickRandomVideo(List<YouTubeVideoSummary> pool) {
        if (pool == null || pool.isEmpty()) {
            throw new CustomException(ErrorCode.VIDEO_NOT_FOUND);
        }

        int index = random.nextInt(pool.size());
        return pool.get(index);
    }

    // 자막 없는 영상이 있을 수 있으므로 최신 영상 pool을 가져온 뒤 랜덤 순서로 섞어서 반환
    public List<YouTubeVideoSummary> collectShuffledLatestVideoPool() {
        List<YouTubeVideoSummary> pool = new ArrayList<>(collectLatestVideoPool());
        Collections.shuffle(pool, random);
        return pool;
    }
}
