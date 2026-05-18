package com.gong.modu.domain.dto.ipo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class IpoSummaryResult {

    private String companySummary;
    private String financialSummary;
    private JsonNode investorProtectionSummary;
    private JsonNode investmentPointSummary;
    private List<RiskItem> riskSummary;

    @Getter
    @NoArgsConstructor
    public static class RiskItem {
        private String title;
        private String content;
    }
}
