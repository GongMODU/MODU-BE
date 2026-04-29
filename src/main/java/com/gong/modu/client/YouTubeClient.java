package com.gong.modu.client;

import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.YouTubeChannelResponse;
import com.gong.modu.domain.dto.YouTubePlaylistItemsResponse;
import com.gong.modu.domain.dto.YouTubeVideoSummary;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

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

    // uploads playlist ID로 최신 5개의 영상을 가져옴
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

        if (response == null || response.items() == null) {
            return List.of();
        }

        return response.items().stream()
                .filter(item -> item.snippet() != null)
                .filter(item -> item.snippet().resourceId() != null)
                .filter(item -> item.snippet().resourceId().videoId() != null)
                .map(item -> new YouTubeVideoSummary(
                        channelId,
                        item.snippet().resourceId().videoId(),
                        item.snippet().title(),
                        item.snippet().publishedAt()
                ))
                .toList();
    }

}
