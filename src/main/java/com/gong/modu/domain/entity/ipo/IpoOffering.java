package com.gong.modu.domain.entity.ipo;

import com.gong.modu.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

// 공모가, 공모주식수, 배정수량을 저장하는 엔티티 (공모조건)
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ipo_offerings")
public class IpoOffering extends BaseTimeEntity {

    @Id // 기본키입니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false) // 하나의 공모 조건은 하나의 공모 이벤트에 대응하므로 일대일 관계
    @JoinColumn(name = "ipo_event_id", nullable = false, unique = true) // 1:1 관계 보장을 위한 unique
    private IpoEvent ipoEvent; // 이 공모 조건이 속한 공모 이벤트

    @Size(max = 50)
    @Column(name = "stock_type", length = 50)
    private String stockType; // 공모 대상 주식 종류

    @PositiveOrZero
    @Column(name = "share_count")
    private Long shareCount; // 새로 공모하는 주식 수

    @PositiveOrZero
    @Column(name = "total_listed_shares")
    private Long totalListedShares; // 상장 후 전체 주식 수

    @PositiveOrZero
    @Column(name = "face_value", precision = 15, scale = 2)
    private BigDecimal faceValue; // 주식 1주의 액면가

    @PositiveOrZero
    @Column(name = "offer_price_min", precision = 15, scale = 2)
    private BigDecimal offerPriceMin; // 기업이 제시한 희망 공모가 범위의 하단

    @PositiveOrZero
    @Column(name = "offer_price_max", precision = 15, scale = 2)
    private BigDecimal offerPriceMax; // 기업이 제시한 희망 공모가 범위의 상단

    @PositiveOrZero
    @Column(name = "offer_price", precision = 15, scale = 2)
    private BigDecimal offerPrice; // 수요 예측 후 최종 확정된 1주당 공모 가격

    @PositiveOrZero
    @Column(name = "offer_amount", precision = 18, scale = 2)
    private BigDecimal offerAmount; // 총 모집금액

    @Size(max = 100)
    @Column(name = "offer_method", length = 100)
    private String offerMethod; // 일반공모, 배정 방식 등 모집 방식

    @PositiveOrZero
    @Column(name = "equal_allocation_shares")
    private Long equalAllocationShares; // 균등배정 주식 수

    @PositiveOrZero
    @Column(name = "general_allocation_shares")
    private Long generalAllocationShares; // 일반 청약자 배정 물량

    @Column(name = "subscription_notice_date")
    private LocalDate subscriptionNoticeDate; // 청약일

    @Column(name = "allocation_base_date")
    private LocalDate allocationBaseDate; // 배정일

    void assignIpoEvent(IpoEvent ipoEvent) {
        this.ipoEvent = ipoEvent; // 이 공모 조건이 어떤 이벤트에 속하는지 연결
    }

    // 공모 조건 갱신하는 메서드
    public void updateOfferingInfo(
            String stockType, // 새 주식 종류
            Long shareCount, // 새 공모주식수
            Long totalListedShares, // 새 상장주식수
            BigDecimal faceValue, // 새 액면가
            BigDecimal offerPriceMin, // 새 희망공모가 하단
            BigDecimal offerPriceMax, // 새 희망공모가 상단
            BigDecimal offerPrice, // 새 확정공모가
            BigDecimal offerAmount, // 새 모집총액
            String offerMethod, // 새 모집 방식
            Long equalAllocationShares, // 새 균등배정 주수
            Long generalAllocationShares // 새 일반배정 주수
    ) {

        this.stockType = stockType;
        this.shareCount = shareCount;
        this.totalListedShares = totalListedShares;
        this.faceValue = faceValue;
        this.offerPriceMin = offerPriceMin;
        this.offerPriceMax = offerPriceMax;
        this.offerPrice = offerPrice;
        this.offerAmount = offerAmount;
        this.offerMethod = offerMethod;
        this.equalAllocationShares = equalAllocationShares;
        this.generalAllocationShares = generalAllocationShares;
    }

    // 청약 공고일, 배정 기준일 갱신 메서드
    public void updateOfferingDates(
            LocalDate subscriptionNoticeDate,
            LocalDate allocationBaseDate
    ) {
        this.subscriptionNoticeDate = subscriptionNoticeDate;
        this.allocationBaseDate = allocationBaseDate;
    }
}
