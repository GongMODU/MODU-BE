package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.IpoEvent;
import com.gong.modu.domain.enums.ipo.IpoEventStatus;
import com.gong.modu.domain.enums.ipo.IpoEventType;
import com.gong.modu.domain.enums.ipo.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// 공모주 일정 및 이벤트 조회 Repository
public interface IpoEventRepository extends JpaRepository<IpoEvent, Long> {

    // 대표 공시 접수번호 기준 조회
    Optional<IpoEvent> findByMainReportRceptNo(String mainReportRceptNo);

    // 특정 상태의 공모 이벤트 조회
    List<IpoEvent> findByStatus(IpoEventStatus status);

    // 특정 시장 공모 이벤트 조회
    List<IpoEvent> findByMarketType(MarketType marketType);

    // 특정 이벤트 유형 조회
    List<IpoEvent> findByEventType(IpoEventType eventType);

    // 청약 예정 공모주 조회
    List<IpoEvent> findBySubscriptionStartDateAfter(LocalDate date);

    // 현재 청약 진행중 공모주 조회
    List<IpoEvent> findBySubscriptionStartDateLessThanEqualAndSubscriptionEndDateGreaterThanEqual(
            LocalDate startDate,
            LocalDate endDate
    );

    // 상장 완료 공모주 조회
    List<IpoEvent> findByListingDateBefore(LocalDate date);

    // 특정 기업의 공모 이벤트 목록 조회
    List<IpoEvent> findByCompanyId(Long companyId);

    // 상장일 기준 최신순 조회
    List<IpoEvent> findAllByOrderByListingDateDesc();
}
