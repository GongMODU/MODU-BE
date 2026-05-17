package com.gong.modu.service.pipeline;

import com.gong.modu.client.DartApiClient;
import com.gong.modu.domain.dto.dart.DartFinancialStatementResponse;
import com.gong.modu.domain.entity.ipo.Company;
import com.gong.modu.domain.entity.ipo.CompanyFinancialHighlight;
import com.gong.modu.domain.enums.ipo.FinancialStatementType;
import com.gong.modu.domain.enums.ipo.ReportCode;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.ipo.CompanyFinancialHighlightRepository;
import com.gong.modu.repository.ipo.CompanyRepository;
import com.gong.modu.util.ExternalNumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// DART 재무제표 API 응답을 company_financial_highlights 테이블에 저장/갱신하는 서비스
public class DartFinancialSyncService {

    private final DartApiClient dartApiClient;
    private final CompanyRepository companyRepository;
    private final CompanyFinancialHighlightRepository financialHighlightRepository;

    @Transactional
    // 특정 기업의 특정 연도/보고서/재무제표 구분에 해당하는 재무 하이라이트를 동기화하는 메서드
    public CompanyFinancialHighlight syncFinancialHighlight(
            Long companyId,
            String businessYear,
            ReportCode reportCode,
            FinancialStatementType financialStatementType
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));

        // DART 단일회사 전체 재무제표 API 호출
        DartFinancialStatementResponse response = dartApiClient.getFinancialStatements(
                company.getCorpCode(),
                businessYear,
                reportCode.getCode(),
                financialStatementType.name()
        );

        // DART 재무제표 응답의 실제 계정과목 목록
        List<DartFinancialStatementResponse.Item> items = response.getList();

        Long revenue = findAmount(items, "매출액");
        Long operatingProfit = findAmount(items, "영업이익");
        Long netIncome = findAmount(items, "당기순이익");
        Long totalAssets = findAmount(items, "자산총계");
        Long totalLiabilities = findAmount(items, "부채총계");
        Long totalEquity = findAmount(items, "자본총계");
        String currency = findCurrency(items);

        return financialHighlightRepository
                .findByCompanyIdAndBsnsYearAndReportCodeAndFinancialStatementType(
                        companyId,
                        businessYear,
                        reportCode,
                        financialStatementType
                ) // 같은 구분의 데이터가 이미 있는지 검사
                .map(existing -> {
                    existing.updateFinancials( // 최신 응답값으로 갱신
                            revenue,
                            operatingProfit,
                            netIncome,
                            totalAssets,
                            totalLiabilities,
                            totalEquity,
                            currency
                    );

                    return existing; // 갱신된 기존 엔티티 반환

                }).orElseGet(() -> financialHighlightRepository.save(
                        CompanyFinancialHighlight.builder()
                                .company(company)
                                .bsnsYear(businessYear)
                                .reportCode(reportCode)
                                .financialStatementType(financialStatementType)
                                .revenue(revenue)
                                .operatingProfit(operatingProfit)
                                .netIncome(netIncome)
                                .totalAssets(totalAssets)
                                .totalLiabilities(totalLiabilities)
                                .totalEquity(totalEquity)
                                .currency(currency)
                                .build()
                ));

    }

    // DART 재무제표 항목 목록에서 특정 계정명에 해당하는 금액을 찾는 메서드
    private Long findAmount(List<DartFinancialStatementResponse.Item> items, String accountNameKeyword) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.stream()
                .filter(item -> item.getAccountNm() != null)
                .filter(item -> item.getAccountNm().contains(accountNameKeyword))
                .map(DartFinancialStatementResponse.Item::getThstrmAmount) // 찾은 항목에서 당기 금액 문자열만 꺼냄
                .map(ExternalNumberParser::toLong)
                .findFirst()
                .orElse(null);
    }

    // 재무제표 항목 목록에서 통화 단위를 찾는 메서드
    private String findCurrency(List<DartFinancialStatementResponse.Item> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.stream()
                .map(DartFinancialStatementResponse.Item::getCurrency)
                .filter(currency -> currency != null && !currency.isBlank())
                .findFirst()
                .orElse(null);
    }
}
