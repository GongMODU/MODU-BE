package com.gong.modu.domain.entity.ipo;

import com.gong.modu.domain.entity.BaseTimeEntity;
import com.gong.modu.domain.enums.ipo.SignalLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 경쟁률, 확약, 보호예수, 신호등 지표를 저장하는 엔티티 (공모지표)
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ipo_metrics")
public class IpoMetric extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false) // 하나의 지표 묶음은 하나의 공모 이벤트에 대응하므로 일대일 관계
    @JoinColumn(name = "ipo_event_id", nullable = false, unique = true) // 1:1 관계를 보장하기 위한 unique
    private IpoEvent ipoEvent; // 이 지표가 속한 공모 이벤트

    @PositiveOrZero // 경쟁률은 음수가 될 수 없습니다.
    @Column(name = "institutional_competition_rate", precision = 10, scale = 2)
    private BigDecimal institutionalCompetitionRate; // 기관경쟁률

    @DecimalMin("0.0000") //
    @DecimalMax("1.0000") // 1.0을 100%로 보는 구조이므로 최대 1입니다.
    @Column(name = "lockup_ratio", precision = 5, scale = 4)
    private BigDecimal lockupRatio; // 의무보유확약 비율

    @DecimalMin("0.0000") //
    @DecimalMax("1.0000") //
    @Column(name = "protective_custody_ratio", precision = 5, scale = 4)
    private BigDecimal protectiveCustodyRatio; // 락업해제 비율 (보호예수 비율)

    @PositiveOrZero
    @Column(name = "general_subscription_rate", precision = 10, scale = 2)
    private BigDecimal generalSubscriptionRate; // 일반청약 경쟁률

    @PositiveOrZero
    @Column(name = "proportional_competition_rate", precision = 10, scale = 2)
    private BigDecimal proportionalCompetitionRate; // 비례배정 경쟁률

    @DecimalMin("0.0000")
    @DecimalMax("1.0000")
    @Column(name = "circulating_shares_ratio", precision = 5, scale = 4)
    private BigDecimal circulatingSharesRatio; // 유통가능물량 비율

    @PositiveOrZero
    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore; // 신호등 계산에 사용하는 내부 위험 점수

    @Enumerated(EnumType.STRING)
    @Column(name = "signal_level", length = 10)
    private SignalLevel signalLevel; // 사용자에게 보여줄 신호등 위험도 (GREEN, YELLOW, RED)

    @Column(name = "computed_at")
    private LocalDateTime computedAt; // 지표와 신호등 점수가 계산된 시각

    void assignIpoEvent(IpoEvent ipoEvent) {
        this.ipoEvent = ipoEvent; // 이 지표가 어떤 공모 이벤트에 속하는지 연결
    }

    // 지표 갱신 메서드
    public void updateMetrics(
            BigDecimal institutionalCompetitionRate, // 새 기관경쟁률
            BigDecimal lockupRatio, // 새 의무보유확약 비율
            BigDecimal protectiveCustodyRatio, // 새 보호예수 비율
            BigDecimal generalSubscriptionRate, // 새 일반청약 경쟁률
            BigDecimal proportionalCompetitionRate, // 새 비례경쟁률
            BigDecimal circulatingSharesRatio, // 새 유통가능물량 비율
            BigDecimal riskScore, // 새 위험 점수
            SignalLevel signalLevel, // 새 신호등 등급
            LocalDateTime computedAt // 새 계산 시각
    ) {
        this.institutionalCompetitionRate = institutionalCompetitionRate;
        this.lockupRatio = lockupRatio;
        this.protectiveCustodyRatio = protectiveCustodyRatio;
        this.generalSubscriptionRate = generalSubscriptionRate;
        this.proportionalCompetitionRate = proportionalCompetitionRate;
        this.circulatingSharesRatio = circulatingSharesRatio;
        this.riskScore = riskScore;
        this.signalLevel = signalLevel;
        this.computedAt = computedAt;
    }

    // 공시 원문 파싱으로 얻은 지표 값을 선택적으로 반영하는 메서드
    public void updateParsedMetrics(
            BigDecimal institutionalCompetitionRate,
            BigDecimal lockupRatio,
            BigDecimal protectiveCustodyRatio
    ) {

        // 기관경쟁률이 파싱된 경우에만 갱신
        if (institutionalCompetitionRate != null) {
            this.institutionalCompetitionRate = institutionalCompetitionRate;
        }

        // 의무보유확약 비율이 파싱된 경우에만 갱신
        if (lockupRatio != null) {
            this.lockupRatio = lockupRatio;
        }

        // 보호예수 비율이 파싱된 경우에만 갱신
        if (protectiveCustodyRatio != null) {
            this.protectiveCustodyRatio = protectiveCustodyRatio;
        }
    }
}
