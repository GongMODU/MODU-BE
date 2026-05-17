package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.Company;
import com.gong.modu.domain.enums.ipo.CorpClass;
import com.gong.modu.domain.enums.ipo.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 기업 기본 정보 조회 Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // DART 고유 기업코드로 조회
    Optional<Company> findByCorpCode(String corpCode);

    // 종목코드로 조회
    Optional<Company> findByStockCode(String stockCode);

    // 기업명 포함 검색
    List<Company> findByCorpNameContaining(String keyword);

    // 특정 시장(KOSPI/KOSDAQ 등)에 속한 기업 목록 조회
    List<Company> findByMarketType(MarketType marketType);

    // 법인 구분 기준 조회
    List<Company> findByCorpClass(CorpClass corpClass);

    // 종목코드 존재 여부 확인
    boolean existsByStockCode(String stockCode);

    // corp_code 존재 여부 확인
    boolean existsByCorpCode(String corpCode);
}
