package com.gong.modu.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "investment_persona_types")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class InvestmentPersonaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "persona_code", nullable = false, length = 10, unique = true)
    private String personaCode;

    @Column(name = "knowledge_axis", nullable = false, length = 2)
    private String knowledgeAxis;

    @Column(name = "risk_axis", nullable = false, length = 2)
    private String riskAxis;

    @Column(name = "korean_name", nullable = false, length = 100)
    private String koreanName;

    @Column(name = "english_name", length = 100)
    private String englishName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "keyword_tags", nullable = false, columnDefinition = "jsonb")
    private String keywordTags;

    @Column(name = "axis_summary", columnDefinition = "TEXT")
    private String axisSummary;

    @Column(name = "persona_description", nullable = false, columnDefinition = "TEXT")
    private String personaDescription;

    @Column(name = "recommended_strategy", columnDefinition = "TEXT")
    private String recommendedStrategy;

    @Column(name = "warning_message", columnDefinition = "TEXT")
    private String warningMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
