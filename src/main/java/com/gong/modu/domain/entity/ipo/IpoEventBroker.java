package com.gong.modu.domain.entity.ipo;

import com.gong.modu.domain.entity.BaseTimeEntity;
import com.gong.modu.domain.enums.ipo.BrokerRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

// 공모 이벤트와 증권사의 연결 정보를 저장하는 엔티티 (공모이벤트-증권사의 N:M 관계의 연결테이블)
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ipo_event_brokers")
public class IpoEventBroker extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공모 이벤트 외래키
    // 하나의 공모 이벤트에는 여러 증권사가 참여할 수 있으므로 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 연결 정보를 조회할 때 항상 공모 이벤트 전체가 필요하진 않으므로 지연 로딩 사용
    @JoinColumn(name = "ipo_event_id", nullable = false)
    private IpoEvent ipoEvent;

    // 증권사 마스터 외래키
    // 하나의 증권사가 여러 공모 이벤트에 참여할 수 있으므로 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id")
    private Broker broker;

    // 증권사명 원문
    @Size(max = 100)
    @Column(name = "broker_name", length = 100, nullable = false)
    private String brokerName;

    // 해당 증권사가 공모에서 맡은 역할
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private BrokerRole role;

    // 인수 대상 주식 종류
    @Size(max = 50)
    @Column(name = "stock_type", length = 50)
    private String stockType;

    // 해당 증권사가 인수하거나 배정받은 주식 수
    @PositiveOrZero
    @Column(name = "underwrite_count")
    private Long underwriteCount;

    // 해당 증권사의 인수 금액
    @PositiveOrZero
    @Column(name = "underwrite_amount", precision = 18, scale = 2)
    private BigDecimal underwriteAmount;

    // 인수 방식
    @Size(max = 100)
    @Column(name = "underwrite_method", length = 100)
    private String underwriteMethod;

    // 공모 이벤트와 증권사 정보를 연결하는 메서드
    // 공시 파싱 이후 정규화된 Broker를 찾았을 때 호출 가능
    public void assignBroker(Broker broker) {
        this.broker = broker;
        this.brokerName = broker.getName();
    }

    // 인수 관련 상세 정보 갱신 메서드 (API 재수집 또는 공시 원문 재파싱 결과 반영 시 사용)
    public void updateUnderwriteInfo(
            BrokerRole role,
            String stockType,
            Long underwriteCount,
            BigDecimal underwriteAmount,
            String underwriteMethod
    ) {
        this.role = role;
        this.stockType = stockType;
        this.underwriteCount = underwriteCount;
        this.underwriteAmount = underwriteAmount;
        this.underwriteMethod = underwriteMethod;
    }
}
