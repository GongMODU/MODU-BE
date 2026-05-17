package com.gong.modu.service.pipeline;

import com.gong.modu.client.KisStockApiClient;
import com.gong.modu.domain.dto.kis.KisCurrentPriceResponse;
import com.gong.modu.domain.dto.kis.KisDailyPriceResponse;
import com.gong.modu.domain.entity.ipo.Company;
import com.gong.modu.domain.entity.ipo.StockPrice;
import com.gong.modu.domain.enums.ipo.StockPriceSource;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.ipo.CompanyRepository;
import com.gong.modu.repository.ipo.StockPriceRepository;
import com.gong.modu.util.ExternalDateParser;
import com.gong.modu.util.ExternalNumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
// KIS 주식 API 응답을 stock_prices 테이블에 저장/갱신하는 서비스
public class KisStockPriceSyncService {

    private final KisStockApiClient kisStockApiClient; // KIS 현재가/기간별 시세 API 호출 담당
    private final CompanyRepository companyRepository;
    private final StockPriceRepository stockPriceRepository;

    @Transactional
    // 특정 기업의 현재가를 KIS API에서 가져와 stock_price에 저장/갱신하는 메서드
    public StockPrice syncCurrentPrice(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));

        if (company.getStockCode() == null || company.getStockCode().isBlank()) {
            throw new CustomException(ErrorCode.STOCK_CODE_NOT_FOUND);
        }

        // KIS 현재가 API 호출
        KisCurrentPriceResponse response = kisStockApiClient.getCurrentPrice(company.getStockCode());

        // 실제 시세 데이터가 담긴 output 객체 꺼냄
        KisCurrentPriceResponse.Output output = response.getOutput();

        // 현재가 데이터는 오늘 날짜 기준으로 저장
        LocalDate today = LocalDate.now();

        BigDecimal currentPrice = ExternalNumberParser.toBigDecimal(output.getCurrentPrice()); // 현재가 변환
        BigDecimal openPrice = ExternalNumberParser.toBigDecimal(output.getOpenPrice()); // 시가 변환
        BigDecimal highPrice = ExternalNumberParser.toBigDecimal(output.getHighPrice()); // 고가 변환
        BigDecimal lowPrice = ExternalNumberParser.toBigDecimal(output.getLowPrice()); // 저가 변환
        Long volume = ExternalNumberParser.toLong(output.getAccumulatedVolume()); // 누적 거래량 변환

        return stockPriceRepository.findByCompanyIdAndTradeDate(companyId, today)
                .map(existing -> { // 오늘 날짜 레코드가 이미 존재한다면 기존 레코드 갱신
                    existing.updatePrice(
                            openPrice,
                            highPrice,
                            lowPrice,
                            null, // 현재가 API에는 종가가 아직 확정되지 않았을 수 있으므로 closePrice는 null 처리
                            currentPrice,
                            volume,
                            StockPriceSource.KIS
                    );

                    return existing;
                })
                .orElseGet(() -> stockPriceRepository.save( // 오늘 날짜 레코드가 없다면 새로 저장
                        StockPrice.builder()
                                .company(company)
                                .tradeDate(today)
                                .openPrice(openPrice)
                                .highPrice(highPrice)
                                .lowPrice(lowPrice)
                                .currentPrice(currentPrice)
                                .volume(volume)
                                .source(StockPriceSource.KIS)
                                .build()
                ));
    }

    @Transactional
    // 특정 기업의 기간별 일봉 시세를 KIS에서 가져와 저장
    public void syncDailyPrices(Long companyId, String startDate, String endDate) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));

        // 종목코드가 없을 시 일별 시세 조회 불가능
        if (company.getStockCode() == null || company.getStockCode().isBlank()) {
            return; // 배치 작업에서는 하나의 기업 때문에 전체 스케줄러가 죽지 않도록 조용히 종료
        }

        // KIS 기간별 시세 API를 호출
        // D => 일봉 데이터
        KisDailyPriceResponse response = kisStockApiClient.getDailyPrices(
                company.getStockCode(),
                startDate,
                endDate,
                "D"
        );

        if (response.getDailyItems() ==  null) { // 응답에 일별 데이터가 없으면 저장할게 없음
            return;
        }

        for (KisDailyPriceResponse.DailyItem item : response.getDailyItems()) {
            upsertDailyPrice(company, item); // 각 일별 시세 목록을 저장 또는 갱신
        }
    }

    // 일별 시세 한 건을 StockPrice 엔티티로 저장/갱신하는 메서드
    private void upsertDailyPrice(Company company, KisDailyPriceResponse.DailyItem item) {
        LocalDate tradeDate = ExternalDateParser.parseBasicDate(item.getTradeDate());

        if (tradeDate == null) // 거래일을 파싱하지 못하면 주가 데이터 기준일이 없으므로 저장 불가
            return; // 메서드 종료

        BigDecimal openPrice = ExternalNumberParser.toBigDecimal(item.getOpenPrice()); // 시가 변환
        BigDecimal highPrice = ExternalNumberParser.toBigDecimal(item.getHighPrice()); // 고가 변환
        BigDecimal lowPrice = ExternalNumberParser.toBigDecimal(item.getLowPrice()); // 저가 변환
        BigDecimal closePrice = ExternalNumberParser.toBigDecimal(item.getClosePrice()); // 종가 변환
        Long volume = ExternalNumberParser.toLong(item.getVolume()); // 거래량 변환

        stockPriceRepository.findByCompanyIdAndTradeDate(company.getId(), tradeDate)
                .ifPresentOrElse( // 기존 레코드가 있다면 값 갱신
                        existing -> existing.updatePrice(
                                openPrice,
                                highPrice,
                                lowPrice,
                                closePrice,
                                closePrice, // 일봉 데이터에서는 currentPrice도 closePrice와 동일하게 저장
                                volume,
                                StockPriceSource.KIS
                        ),

                        () -> stockPriceRepository.save( // 기존 레코드가 없다면 새 StockPrice 저장
                                StockPrice.builder()
                                        .company(company)
                                        .tradeDate(tradeDate)
                                        .openPrice(openPrice)
                                        .highPrice(highPrice)
                                        .lowPrice(lowPrice)
                                        .closePrice(closePrice)
                                        .currentPrice(closePrice)
                                        .volume(volume)
                                        .source(StockPriceSource.KIS)
                                        .build()
                        )
                );
    }
}
