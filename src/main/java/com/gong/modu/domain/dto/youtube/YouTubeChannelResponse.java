package com.gong.modu.domain.dto.youtube;

import java.util.List;

public record YouTubeChannelResponse(
        List<Item> items
) {
    public record Item(
            String id,
            Snippet snippet,
            ContentDetails contentDetails
    ) {
    }
    public record Snippet(
            String title,
            String description,
            String customUrl
    ) {
    }

    public record ContentDetails(
            RelatedPlaylists relatedPlaylists
    ) {
    }

    public record RelatedPlaylists(
            String uploads
    ) {
    }
}
