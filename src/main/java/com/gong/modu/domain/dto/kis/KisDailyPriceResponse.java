package com.gong.modu.domain.dto.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// KIS 국내주식 기간별 시세 API 응답 DTO - /uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice
// 특정 종목의 일/주/월/년 단위 시세 이력을 가져오기 위해 사용
@Getter
@NoArgsConstructor
public class KisDailyPriceResponse {

    // KIS 응답 결과 코드 (정상: 0)
    @JsonProperty("rt_cd")
    private String resultCode;

    @JsonProperty("msg_cd")
    private String messageCode;

    @JsonProperty("msg1")
    private String message;

    // 기간별 시세 목록
    // KIS 응답에서는 output2에 일별 데이터 배열이 들어옴
    @JsonProperty("output2")
    private List<DailyItem> dailyItems;

    // 일별 시세 한 건을 표현하는 DTO
    @Getter
    @NoArgsConstructor
    public static class DailyItem {

        @JsonProperty("stck_bsop_date")
        private String tradeDate; // 거래일 (yyyyMMdd)

        @JsonProperty("stck_oprc")
        private String openPrice; // 시가

        @JsonProperty("stck_hgpr")
        private String highPrice; // 고가

        @JsonProperty("stck_lwpr")
        private String lowPrice; // 저가

        @JsonProperty("stck_clpr")
        private String closePrice; // 종가

        @JsonProperty("acml_vol")
        private String volume; // 누적 거래량
    }
}
