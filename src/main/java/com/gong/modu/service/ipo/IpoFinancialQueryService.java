package com.gong.modu.service.ipo;

import com.gong.modu.domain.dto.ipo.IpoFinancialResponse;
import com.gong.modu.domain.entity.ipo.CompanyFinancialHighlight;
import com.gong.modu.domain.entity.ipo.IpoEvent;
import com.gong.modu.domain.enums.ipo.FinancialStatementType;
import com.gong.modu.domain.enums.ipo.ReportCode;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.ipo.CompanyFinancialHighlightRepository;
import com.gong.modu.repository.ipo.IpoEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 재무 차트용 연간 재무 하이라이트 조회 서비스
// CFS(연결) 우선, 없으면 OFS(개별) 폴백 / 최신 2개년만 반환
@Service
@RequiredArgsConstructor
public class IpoFinancialQueryService {

    private final IpoEventRepository ipoEventRepository;
    private final CompanyFinancialHighlightRepository financialRepository;

    @Transactional(readOnly = true)
    public List<IpoFinancialResponse> getFinancials(Long ipoEventId) {
        // ipoEventId → company 경로로 조회 / 이벤트 없으면 404
        IpoEvent event = ipoEventRepository.findById(ipoEventId)
                .orElseThrow(() -> new CustomException(ErrorCode.IPO_EVENT_NOT_FOUND));

        Long companyId = event.getCompany().getId();

        // ANNUAL만 조회해 분기 데이터 혼입 방지 / 최신순 정렬로 반환
        List<CompanyFinancialHighlight> annuals =
                financialRepository.findByCompanyIdAndReportCodeOrderByBsnsYearDesc(companyId, ReportCode.ANNUAL);

        // 동일 연도에 CFS·OFS 둘 다 존재할 경우 CFS 유지, OFS는 CFS 없는 연도에만 사용
        Map<String, CompanyFinancialHighlight> byYear = annuals.stream()
                .collect(Collectors.toMap(
                        CompanyFinancialHighlight::getBsnsYear,
                        h -> h,
                        (existing, incoming) ->
                                existing.getFinancialStatementType() == FinancialStatementType.CFS ? existing : incoming
                ));

        // 최신 2개년 선택 후 오름차순 재정렬 (프론트 차트 x축 기준)
        return byYear.values().stream()
                .sorted(Comparator.comparing(CompanyFinancialHighlight::getBsnsYear).reversed())
                .limit(2)
                .sorted(Comparator.comparing(CompanyFinancialHighlight::getBsnsYear))
                .map(h -> IpoFinancialResponse.builder()
                        .year(h.getBsnsYear())
                        .revenue(h.getRevenue())
                        .operatingProfit(h.getOperatingProfit())
                        .netIncome(h.getNetIncome())
                        .totalAssets(h.getTotalAssets())
                        .totalLiabilities(h.getTotalLiabilities())
                        .totalEquity(h.getTotalEquity())
                        .build())
                .collect(Collectors.toList());
    }
}
