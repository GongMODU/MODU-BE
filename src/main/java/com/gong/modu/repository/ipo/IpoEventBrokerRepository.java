package com.gong.modu.repository.ipo;

import com.gong.modu.domain.enums.ipo.BrokerRole;
import com.gong.modu.domain.entity.ipo.IpoEventBroker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 공모 이벤트와 증권사 연결 정보 조회 Repository
public interface IpoEventBrokerRepository extends JpaRepository<IpoEventBroker, Long> {

    // 특정 공모 이벤트에 참여한 증권사 목록 조회
    List<IpoEventBroker> findByIpoEventId(Long ipoEventId);

    // 특정 증권사가 참여한 공모 이벤트 연결 목록 조회
    List<IpoEventBroker> findByBrokerId(Long brokerId);

    // 특정 공모 이벤트에서 특정 역할을 맡은 증권사 목록 조회
    List<IpoEventBroker> findByIpoEventIdAndRole(Long ipoEventId, BrokerRole role);

    // 원문 증권사명 기준 조회
    List<IpoEventBroker> findByBrokerNameContaining(String brokerName);
}
