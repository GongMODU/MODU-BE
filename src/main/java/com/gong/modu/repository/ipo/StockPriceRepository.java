package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.StockPrice;
import com.gong.modu.domain.enums.ipo.StockPriceSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// 주가 및 거래량 이력 조회 Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    // 특정 기업의 특정 거래일 주가 조회
    Optional<StockPrice> findByCompanyIdAndTradeDate(Long companyId, LocalDate tradeDate);

    // 특정 기업의 전체 주가 이력 조회
    List<StockPrice> findByCompanyId(Long companyId);

    // 특정 기업의 주가 이력을 거래일 내림차순으로 조회
    List<StockPrice> findByCompanyIdOrderByTradeDateDesc(Long companyId);

    // 특정 기업의 특정 기간 주가 이력 조회
    List<StockPrice> findByCompanyIdAndTradeDateBetweenOrderByTradeDateAsc(
            Long companyId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 데이터 출처 기준 조회
    List<StockPrice> findBySource(StockPriceSource source);
}
