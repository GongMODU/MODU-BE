package com.gong.modu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// 외부 HTTP API 호출에 사용할 WebClient Bean들을 등록하는 설정 클래스
@Configuration
public class WebClientConfig {

    @Value("${external.dart.base-url}")
    private String dartBaseUrl;

    @Value("${external.kis.base-url}")
    private String kisBaseUrl;

    @Bean
    public WebClient dartWebClient() {
        return WebClient.builder()
                .baseUrl(dartBaseUrl)
                .build();
    }

    @Bean
    public WebClient kisWebClient() {
        return WebClient.builder()
                .baseUrl(kisBaseUrl)
                .build();
    }

    @Bean
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/youtube/v3")
                .build();
    }
}
