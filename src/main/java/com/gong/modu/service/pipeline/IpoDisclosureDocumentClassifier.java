package com.gong.modu.service.pipeline;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IpoDisclosureDocumentClassifier {
    private static final List<String> IPO_KEYWORDS = List.of(
            "증권신고서",
            "지분증권",
            "투자설명서",
            "공모",
            "공모가",
            "희망공모가",
            "확정공모가",
            "청약",
            "수요예측",
            "기관투자자",
            "의무보유확약",
            "보호예수",
            "상장예정",
            "모집",
            "매출"
    );

    private static final List<String> NON_IPO_KEYWORDS = List.of(
            "타법인 주식 및 출자증권 양도결정",
            "타법인 주식 및 출자증권 취득결정",
            "단일판매",
            "공급계약",
            "최대주주 변경",
            "임원ㆍ주요주주",
            "주식등의대량보유상황보고서",
            "사업보고서",
            "반기보고서",
            "분기보고서"
    );

    public boolean isIpoCandidate(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = normalize(text);

        boolean hasDisclosureTitle =
                normalized.contains("증권신고서")
                        || normalized.contains("투자설명서")
                        || normalized.contains("정정신고서");

        boolean hasEquityKeyword =
                normalized.contains("지분증권")
                        || normalized.contains("기명식 보통주")
                        || normalized.contains("모집 또는 매출");

        boolean hasIpoScheduleKeyword =
                normalized.contains("청약")
                        || normalized.contains("수요예측")
                        || normalized.contains("공모가")
                        || normalized.contains("상장예정");

        return hasDisclosureTitle && hasEquityKeyword && hasIpoScheduleKeyword;
    }

    public List<String> findMatchedIpoKeywords(String text) {

        List<String> result = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return result;
        }

        String normalized = normalize(text);

        for (String keyword : IPO_KEYWORDS) {
            if (normalized.contains(keyword)) {
                result.add(keyword);
            }
        }

        return result;
    }

    public String detectDocumentType(String text) {

        if (text == null || text.isBlank()) {
            return "UNKNOWN";
        }

        String normalized = normalize(text);

        if (normalized.contains("증권신고서")) {
            return "증권신고서";
        }

        if (normalized.contains("투자설명서")) {
            return "투자설명서";
        }

        if (normalized.contains("정정신고서")) {
            return "정정신고서";
        }

        for (String keyword : NON_IPO_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return keyword;
            }
        }

        return "UNKNOWN";
    }

    private String normalize(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

}
