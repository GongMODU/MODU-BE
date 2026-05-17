package com.gong.modu.repository.ipo;

import com.gong.modu.domain.entity.ipo.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 증권사 마스터 조회 Repository
public interface BrokerRepository extends JpaRepository<Broker, Long> {
    // 증권사명으로 조회
    Optional<Broker> findByName(String name);

    // 증권사 축약명으로 조회
    Optional<Broker> findByShortName(String shortName);

    // 증권사명에 특정 키워드가 포함된 목록 조회
    List<Broker> findByNameContaining(String keyword);

    // 증권사명 존재 여부 확인
    boolean existsByName(String name);
}
