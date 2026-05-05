package com.gong.modu.client;

import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.YouTubeChannelResponse;
import com.gong.modu.domain.dto.YouTubePlaylistItemsResponse;
import com.gong.modu.domain.dto.YouTubeVideoSummary;
import com.gong.modu.domain.dto.YouTubeVideosResponse;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// YouTube Data API와 직접 통신하는 클라이언트 클래스
@Component
public class YouTubeClient {
    private final WebClient youtubeWebClient;
    private final YouTubeProperties properties;

    public YouTubeClient(WebClient youtubeWebClient, YouTubeProperties properties) {
        this.youtubeWebClient = youtubeWebClient;
        this.properties = properties;
    }

    // 특정 채널 ID로 채널 기본 정보를 조회
    // part=snippet (채널명, 설명 같은 기본 정보 조회)
    public YouTubeChannelResponse getChannel(String channelId) {
        return youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", "snippet")
                        .queryParam("id", channelId)
                        .queryParam("key", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(YouTubeChannelResponse.class)
                .block();
    }

    // 채널 ID로 uploads playlist ID를 조회
    // part=contentDetails: 채널의 관련 플레이리스트 정보 조회
    // 이 중 relatedPlaylists.uploads가 업로드 영상 목록 플레이리스트
    public String getUploadsPlaylistId(String channelId) {
        YouTubeChannelResponse response = youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/channels")
                        .queryParam("part", "contentDetails")
                        .queryParam("id", channelId)
                        .queryParam("key", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(YouTubeChannelResponse.class)
                .block();

        if (response == null || response.items() == null || response.items().isEmpty()) {
            throw new CustomException(ErrorCode.CHANNEL_NOT_FOUND);
        }

        return response.items()
                .get(0)
                .contentDetails()
                .relatedPlaylists()
                .uploads();
    }

    // uploads playlist ID로 최신 영상을 가져옴
    // playListItems API에서는 영상 길이(duration)를 바로 받을 수 없으므로 videoId 목록만 가져온 뒤, videos API를 한 번 더 호출해야 함
    public List<YouTubeVideoSummary> getLatestVideos(String channelId, int maxResults) {
        String uploadsPlaylistId = getUploadsPlaylistId(channelId);

        YouTubePlaylistItemsResponse response = youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playlistItems")
                        .queryParam("part", "snippet")
                        .queryParam("playlistId", uploadsPlaylistId)
                        .queryParam("maxResults", maxResults)
                        .queryParam("key", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(YouTubePlaylistItemsResponse.class)
                .block();

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return List.of();
        }

        // playlistItems 응답에서 videoId만 모음
        List<String> videoIds = response.items().stream()
                .filter(item -> item.snippet() != null)
                .filter(item -> item.snippet().resourceId() != null)
                .filter(item -> item.snippet().resourceId().videoId() != null)
                .map(item -> item.snippet().resourceId().videoId())
                .toList();

        if (videoIds.isEmpty()) {
            return List.of();
        }

        // videoId별 영상 길이를 조회
        Map<String, Long> durationSecondsByVideoId = getDurationSecondsByVideoId(videoIds);

        // 기존 snippet 정보 + 새로 조회한 durationSeconds를 합쳐서 YouTubeVideoSummary 생성
        return response.items().stream()
                .filter(item -> item.snippet() != null)
                .filter(item -> item.snippet().resourceId() != null)
                .filter(item -> item.snippet().resourceId().videoId() != null)
                .map(item -> {
                    String videoId = item.snippet().resourceId().videoId();

                    long durationSeconds = durationSecondsByVideoId.getOrDefault(videoId, 0L);

                    return new YouTubeVideoSummary(
                            channelId,
                            item.snippet().channelTitle(),
                            videoId,
                            item.snippet().title(),
                            item.snippet().publishedAt(),
                            durationSeconds
                    );
                })
                .toList();
    }

    // 여러 videoId에 대해 videos API를 호출하여 영상 길이를 초 단위로 가져옴
    private Map<String, Long> getDurationSecondsByVideoId(List<String> videoIds) {
        String joinedVideoIds = String.join(",", videoIds);

        YouTubeVideosResponse response = youtubeWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/videos")
                        .queryParam("part", "contentDetails")
                        .queryParam("id", joinedVideoIds)
                        .queryParam("key", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(YouTubeVideosResponse.class)
                .block();

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return Map.of();
        }

        return response.items().stream()
                .filter(item -> item.id() != null)
                .filter(item -> item.contentDetails() != null)
                .filter(item -> item.contentDetails().duration() != null)
                .collect(Collectors.toMap(
                        YouTubeVideosResponse.Item::id,
                        item -> parseDurationSeconds(item.contentDetails().duration())
                ));
    }

    // YouTube API의 ISO-8601 duration 문자열을 초 단위로 변환
    private long parseDurationSeconds(String isoDuration) {
        if (isoDuration == null || isoDuration.isBlank()) {
            return 0;
        }

        return Duration.parse(isoDuration).getSeconds();
    }

}
