package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.IpoDisclosureReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 공시 원문 요약 조회 Repository
public interface IpoDisclosureReportRepository extends JpaRepository<IpoDisclosureReport, Long> {

    // 특정 공모 이벤트의 공시 목록 조회
    List<IpoDisclosureReport> findByIpoEventId(Long ipoEventId);

    // DART 접수번호 기준 공시 조회
    Optional<IpoDisclosureReport> findByRceptNo(String rceptNo);

    // 특정 공모 이벤트 안에서 특정 접수번호 공시 조회
    Optional<IpoDisclosureReport> findByIpoEventIdAndRceptNo(Long ipoEventId, String rceptNo);

    // 공시명에 특정 키워드가 포함된 공시 목록 조회
    List<IpoDisclosureReport> findByReportNameContaining(String keyword);

    // AI 요약이 아직 생성되지 않은 공시 목록 조회 (배치 대상)
    List<IpoDisclosureReport> findByCompanySummaryIsNull();
}
