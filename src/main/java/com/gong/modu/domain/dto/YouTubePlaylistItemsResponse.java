package com.gong.modu.domain.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record YouTubePlaylistItemsResponse(
        List<Item> items
) {
    public record Item(
            String id,
            Snippet snippet
    ) {}
    public record Snippet(
            OffsetDateTime publishedAt,
            String channelTitle,
            String title,
            String description,
            ResourceId resourceId
    ){}
    public record ResourceId(
            String kind,
            String videoId
    ){}
}
