package com.gong.modu.client;

import com.gong.modu.domain.dto.kis.KisTokenResponse;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

// KIS 접근 토큰 발급과 캐싱을 담당하는 Client 클래스
@Component
@RequiredArgsConstructor
public class KisTokenClient {

    private static final String KIS_ACCESS_TOKEN_KEY = "kis:access-token"; // Redis에 KIS 접근 토큰을 저장할 때 사용할 key
    private final WebClient kisWebClient;
    private final StringRedisTemplate redisTemplate; // Redis 문자열 저장/조회용 Template

    @Value("${external.kis.app-key}")
    private String appKey;

    @Value("${external.kis.app-secret}")
    private String appSecret;

    // KIS access token을 반환하는 메서드
    public String getAccessToken() {
        String cachedToken = redisTemplate.opsForValue().get(KIS_ACCESS_TOKEN_KEY);
        if (cachedToken != null) {
            return cachedToken;
        }

        return issueAccessToken();
    }

    // KIS 토큰 발급 API를 호출하는 내부 메서드 (Redis에 토큰이 없을 때만 호출)
    private String issueAccessToken() {
        try {
            Map<String, String> requestBody = Map.of(
                    "grant_type", "client_credentials",
                    "appkey", appKey,
                    "appsecret", appSecret
            );

            KisTokenResponse response = kisWebClient.post()
                    .uri("/oauth2/tokenP")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(KisTokenResponse.class)
                    .block();

            // 응답 자체가 없거나 access token이 비어 있으면 토큰 발급 실패로 처리
            if (response == null || response.getAccessToken() == null) {
                throw new CustomException(ErrorCode.KIS_TOKEN_ERROR);
            }

            // 발급받은 access token을 Redis에 저장
            redisTemplate.opsForValue()
                    .set(KIS_ACCESS_TOKEN_KEY, response.getAccessToken(), Duration.ofHours(23));

            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            throw new CustomException(ErrorCode.KIS_TOKEN_ERROR);
        }
    }
}
