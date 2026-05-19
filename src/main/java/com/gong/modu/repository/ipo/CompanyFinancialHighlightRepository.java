package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.CompanyFinancialHighlight;
import com.gong.modu.domain.enums.ipo.FinancialStatementType;
import com.gong.modu.domain.enums.ipo.ReportCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 기업 재무 하이라이트 조회 Repository
public interface CompanyFinancialHighlightRepository extends JpaRepository<CompanyFinancialHighlight, Long> {

    // 특정 기업의 모든 재무 하이라이트 조회
    List<CompanyFinancialHighlight> findByCompanyId(Long companyId);

    // 특정 기업의 특정 사업연도 재무 하이라이트 조회
    List<CompanyFinancialHighlight> findByCompanyIdAndBsnsYear(Long companyId, String bsnsYear);

    // 특정 기업의 특정 사업연도, 보고서, 재무제표 구분 기준 조회
    Optional<CompanyFinancialHighlight> findByCompanyIdAndBsnsYearAndReportCodeAndFinancialStatementType(
            Long companyId,
            String bsnsYear,
            ReportCode reportCode,
            FinancialStatementType financialStatementType
    );

    // 특정 기업의 최신 사업연도 순 재무정보 조회
    List<CompanyFinancialHighlight> findByCompanyIdOrderByBsnsYearDesc(Long companyId);

    // 특정 기업의 특정 보고서 유형만 최신 사업연도 순으로 조회
    // ANNUAL만 지정 시 분기 데이터 혼입 방지 / 재무 차트 조회에서 사용
    List<CompanyFinancialHighlight> findByCompanyIdAndReportCodeOrderByBsnsYearDesc(
            Long companyId, ReportCode reportCode);
}
