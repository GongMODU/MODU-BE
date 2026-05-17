package com.gong.modu.domain.entity.ipo;

import com.gong.modu.domain.entity.BaseTimeEntity;
import com.gong.modu.domain.enums.ipo.StockPriceSource;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// 상장 후 주가와 거래량 이력을 저장하는 엔티티
// KIS/KRX API에서 수집한 일별 시세 데이터를 관리하기 위해 사용
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "stock_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_stock_prices_company_trade_date",
                        columnNames = {"company_id", "trade_date"}
                )
        }
)
public class StockPrice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 기업의 주가 데이터인지 나타내는 외래키
    // 한 기업은 날짜별로 여러 개의 주가 데이터를 가지므로 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 거래일
    // 같은 기업이라도 날짜별로 주가가 다르므로 필수값
    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    // 시가 (해당 거래일에 처음 형성된 가격)
    @PositiveOrZero
    @Column(name = "open_price", precision = 15, scale = 2)
    private BigDecimal openPrice;

    // 고가 (해당 거래일 중 가장 높았던 가격)
    @PositiveOrZero
    @Column(name = "high_price", precision = 15, scale = 2)
    private BigDecimal highPrice;

    // 저가 (해당 거래일 중 가장 낮았던 가격)
    @PositiveOrZero
    @Column(name = "low_price", precision = 15, scale = 2)
    private BigDecimal lowPrice;

    // 종가 (해당 거래일 장 마감 기준 가격)
    @PositiveOrZero
    @Column(name = "close_price", precision = 15, scale = 2)
    private BigDecimal closePrice;

    // 현재가
    // KIS 현재가 API로 가져온 실시간 또는 준실시간 가격을 저장할 수 있음
    @PositiveOrZero
    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;

    // 거래량 (해당 거래일에 거래된 주식 수)
    @PositiveOrZero
    @Column(name = "volume")
    private Long volume;

    // 주가 데이터 수집 출처 (KIS 또는 KRX 중 하나)
    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 10, nullable = false)
    private StockPriceSource source;

    // 주가 정보를 갱신하는 메서드: KIS/KRX API 재수집 결과가 들어왔을 때 사용
    public void updatePrice(
            BigDecimal openPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal closePrice,
            BigDecimal currentPrice,
            Long volume,
            StockPriceSource source
    ) {
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.currentPrice = currentPrice;
        this.volume = volume;
        this.source = source;
    }
}
