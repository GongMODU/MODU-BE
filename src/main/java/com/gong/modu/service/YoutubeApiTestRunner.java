package com.gong.modu.service;

import com.gong.modu.client.YouTubeClient;
import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.YouTubeChannelResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// API 호출 테스트용
// @Component
public class YoutubeApiTestRunner implements CommandLineRunner {
    private final YouTubeClient youTubeClient;
    private final YouTubeProperties properties;

    public YoutubeApiTestRunner(YouTubeClient youTubeClient, YouTubeProperties properties) {
        this.youTubeClient = youTubeClient;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        String channelId = properties.getChannelIds().get(0);

        YouTubeChannelResponse response = youTubeClient.getChannel(channelId);

        if (response.items() == null || response.items().isEmpty()) {
            System.out.println("채널을 찾지 못했습니다.");
            return;
        }

        String channelTitle = response.items().get(0).snippet().title();
        System.out.println("조회된 채널명 = " + channelTitle);
    }
}
