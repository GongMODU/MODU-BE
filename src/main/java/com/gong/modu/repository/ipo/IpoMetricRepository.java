package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.IpoMetric;
import com.gong.modu.domain.enums.ipo.SignalLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 공모주 지표 조회 Repository
public interface IpoMetricRepository extends JpaRepository<IpoMetric, Long> {

    // 공모 이벤트 기준 조회
    Optional<IpoMetric> findByIpoEventId(Long ipoEventId);

    // 신호등 지표 기준 조회
    List<IpoMetric> findBySignalLevel(SignalLevel signalLevel);
}
