package com.gong.modu.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageHomeResponse {

    private String nickname;
    private String provider;
    private String email;
    private InvestmentProfileInfo investmentProfile;

    @Getter
    @Builder
    public static class InvestmentProfileInfo {
        private Long id;
        private String personaCode;
        private String koreanName;
        private String englishName;
        private String keywordTags;
        private String axisSummary;
        private String personaDescription;
        private String recommendedStrategy;
        private String warningMessage;
    }
}
