package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.IpoOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 공모 조건 조회 Repository
public interface IpoOfferingRepository extends JpaRepository<IpoOffering, Long> {
    // 공모 이벤트 기준 조회
    Optional<IpoOffering> findByIpoEventId(Long ipoEventId);
}
