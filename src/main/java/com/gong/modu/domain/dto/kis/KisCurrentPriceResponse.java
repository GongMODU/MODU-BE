package com.gong.modu.domain.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// KIS 국내주식 현재가 시세 API 응답 DTO - /uapi/domestic-stock/v1/quotations/inquire-price
// 상장 후 현재가, 시가, 고가, 저가, 거래량 등을 가져오기 위해 사용
@Getter
@NoArgsConstructor
public class KisCurrentPriceResponse {

    // KIS 응답 결과 코드 (정상: 0)
    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    // 실제 현재가 데이터 본문
    private Output output;

    // 현재가 API의 실제 시세 필드를 담는 내부 DTO
    @Getter
    @NoArgsConstructor
    public static class Output {

        @JsonProperty("stck_prpr")
        private String currentPrice; // 현재가

        @JsonProperty("prdy_vrss")
        private String previousDayDifference; // 전일 대비 가격 차이

        @JsonProperty("acml_vol")
        private String accumulatedVolume; // 누적 거래량

        @JsonProperty("stck_oprc")
        private String openPrice; // 시가

        @JsonProperty("stck_hgpr")
        private String highPrice; // 고가

        @JsonProperty("stck_lwpr")
        private String lowPrice; // 저가
    }
}
