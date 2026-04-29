package com.gong.modu.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class InvestmentAnalysisResponse {

    private String personaCode;
    private String knowledgeLevel;
    private String riskLevel;
    private String koreanName;
    private String englishName;
    private String keywordTags;
    private String axisSummary;
    private String personaDescription;
    private String recommendedStrategy;
    private String warningMessage;
    private Map<String, Integer> knowledgeScoreMap;
    private Map<String, Integer> riskScoreMap;
}
