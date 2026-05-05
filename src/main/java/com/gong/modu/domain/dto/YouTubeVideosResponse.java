package com.gong.modu.domain.dto;

import java.util.List;

// YouTube Data API의 /videos 응답을 받기 위한 DTO
// 영상 길이는 playlistItems API가 아니라 videos API의 contentDetails.duration에 들어 있기 때문에 별도로 dto 생성함
public record YouTubeVideosResponse(
        List<Item> items
) {
    public record Item(
            String id,
            ContentDetails contentDetails
    ) {}

    public record ContentDetails(
            String duration
    ) {}
}
