package com.gong.modu.domain.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// KIS 접근 토큰 발급 응답 DTO - /oauth2/tokenP
@Getter
@NoArgsConstructor
public class KisTokenResponse {

    // Authorization 헤더에 넣을 접근 토큰
    @JsonProperty("access_token") // KIS 응답 필드명을 그대로 매핑하기 위해 @JsonProperty 사용
    private String accessToken;

    // 접근 토큰 만료 시각
    @JsonProperty("access_token_token_expired")
    private String accessTokenExpiredAt;

    // 토큰 타입
    @JsonProperty("token_type")
    private String tokenType;

    // 만료까지 남은 시간
    @JsonProperty("expires_in")
    private Long expiresIn;
}
