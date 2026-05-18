package com.gong.modu.domain.dto.ipo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IpoDisclosureReportResponse {

    private String companySummary;
    private String financialSummary;
    private JsonNode investorProtectionSummary;
    private JsonNode investmentPointSummary;
    private List<RiskItem> riskSummary;
    private String summaryVersion;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskItem {
        private String title;
        private String content;
    }
}
