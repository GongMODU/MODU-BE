package com.gong.modu.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_investment_profile_sessions",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserInvestmentProfileSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_type_id", nullable = false)
    private InvestmentPersonaType personaType;

    @Column(name = "q1_investment_goal", nullable = false)
    private String q1InvestmentGoal;

    @Column(name = "q2_experience_count", nullable = false)
    private String q2ExperienceCount;

    @Column(name = "q3_term_knowledge", nullable = false)
    private String q3TermKnowledge;

    @Column(name = "q4_reading_ability", nullable = false)
    private String q4ReadingAbility;

    @Column(name = "q5_available_funds", nullable = false)
    private String q5AvailableFunds;

    @Column(name = "q6_loss_behavior", nullable = false)
    private String q6LossBehavior;

    @Column(name = "q7_ipo_asset_ratio", nullable = false)
    private String q7IpoAssetRatio;

    @Column(name = "q8_red_signal_willingness", nullable = false)
    private String q8RedSignalWillingness;

    @Column(name = "q9_asset_management", nullable = false)
    private String q9AssetManagement;

    @Column(name = "q10_profit_timing", nullable = false)
    private String q10ProfitTiming;

    @Column(name = "knowledge_axis_result", nullable = false, length = 2)
    private String knowledgeAxisResult;

    @Column(name = "risk_axis_result", nullable = false, length = 2)
    private String riskAxisResult;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "knowledge_score_map", nullable = false, columnDefinition = "jsonb")
    private String knowledgeScoreMap;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risk_score_map", nullable = false, columnDefinition = "jsonb")
    private String riskScoreMap;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @Column(name = "recommended_strategy", columnDefinition = "TEXT")
    private String recommendedStrategy;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public void update(
            InvestmentPersonaType personaType,
            String q1InvestmentGoal,
            String q2ExperienceCount,
            String q3TermKnowledge,
            String q4ReadingAbility,
            String q5AvailableFunds,
            String q6LossBehavior,
            String q7IpoAssetRatio,
            String q8RedSignalWillingness,
            String q9AssetManagement,
            String q10ProfitTiming,
            String knowledgeAxisResult,
            String riskAxisResult,
            String knowledgeScoreMap,
            String riskScoreMap,
            String resultSummary,
            String recommendedStrategy
    ) {
        this.personaType = personaType;
        this.q1InvestmentGoal = q1InvestmentGoal;
        this.q2ExperienceCount = q2ExperienceCount;
        this.q3TermKnowledge = q3TermKnowledge;
        this.q4ReadingAbility = q4ReadingAbility;
        this.q5AvailableFunds = q5AvailableFunds;
        this.q6LossBehavior = q6LossBehavior;
        this.q7IpoAssetRatio = q7IpoAssetRatio;
        this.q8RedSignalWillingness = q8RedSignalWillingness;
        this.q9AssetManagement = q9AssetManagement;
        this.q10ProfitTiming = q10ProfitTiming;
        this.knowledgeAxisResult = knowledgeAxisResult;
        this.riskAxisResult = riskAxisResult;
        this.knowledgeScoreMap = knowledgeScoreMap;
        this.riskScoreMap = riskScoreMap;
        this.resultSummary = resultSummary;
        this.recommendedStrategy = recommendedStrategy;
        this.analyzedAt = LocalDateTime.now();
    }
}
