package com.gong.modu.client;

import com.gong.modu.domain.dto.kis.KisCurrentPriceResponse;
import com.gong.modu.domain.dto.kis.KisDailyPriceResponse;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// KIS 주식 시세 API 호출을 담당하는 Client 클래스
@Component
@RequiredArgsConstructor
public class KisStockApiClient {

    private final WebClient kisWebClient;
    private final KisTokenClient kisTokenClient;

    @Value("${external.kis.app-key}")
    private String appKey;

    @Value("${external.kis.app-secret}")
    private String appSecret;

    // KIS 현재가 API 호출
    // 목적: 상장 후 현재가 조회, 현재가, 시가, 고가, 저가, 누적거래량 확보
    public KisCurrentPriceResponse getCurrentPrice(String stockCode) { // 파라미터: 국내 주식 종목코드 6자리
        try {
            String accessToken = kisTokenClient.getAccessToken();
            KisCurrentPriceResponse response = kisWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                            .queryParam("fid_cond_mrkt_div_code", "J")
                            .queryParam("fid_input_iscd", stockCode)
                            .build())
                    .header("authorization", "Bearer " + accessToken)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "FHKST01010100")
                    .retrieve()
                    .bodyToMono(KisCurrentPriceResponse.class)
                    .block();

            if (response == null) {
                throw new CustomException(ErrorCode.EXTERNAL_API_EMPTY_RESPONSE);
            }
            validateKisResult(response.getResultCode());
            return response;

        } catch (WebClientResponseException e) {
            throw new CustomException(ErrorCode.KIS_API_ERROR);
        }
    }

    // KIS 기간별 시세 API 호출
    // 목적: 특정 종목의 일별/주별/월별/년별 시세 이력 조회, stock_prices 테이블의 시가, 고가, 저가, 종가, 거래량 저장
    public KisDailyPriceResponse getDailyPrices(
            String stockCode,
            String startDate,
            String endDate,
            String periodCode // D: 일봉, W: 주봉, M: 월봉, Y: 년봉
    ) {
        try {
            String accessToken = kisTokenClient.getAccessToken();
            KisDailyPriceResponse response = kisWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                            .queryParam("fid_cond_mrkt_div_code", "J")
                            .queryParam("fid_input_iscd", stockCode)
                            .queryParam("fid_input_date_1", startDate)
                            .queryParam("fid_input_date_2", endDate)
                            .queryParam("fid_period_div_code", periodCode)
                            .queryParam("fid_org_adj_prc", "0")
                            .build())
                    .header("authorization", "Bearer " + accessToken)
                    .header("appkey", appKey)
                    .header("appsecret", appSecret)
                    .header("tr_id", "FHKST03010100")
                    .retrieve()
                    .bodyToMono(KisDailyPriceResponse.class)
                    .block();

            if (response == null) {
                throw new CustomException(ErrorCode.EXTERNAL_API_EMPTY_RESPONSE);
            }

            validateKisResult(response.getResultCode());
            return response;

        } catch (WebClientResponseException e) {
            throw new CustomException(ErrorCode.KIS_API_ERROR);
        }
    }

    // KIS 응답 결과 코드 검증 메서드
    private void validateKisResult(String resultCode) {
        if (!"0".equals(resultCode)) { // 정상 응답: 0
            throw new CustomException(ErrorCode.KIS_API_ERROR);
        }
    }
}
