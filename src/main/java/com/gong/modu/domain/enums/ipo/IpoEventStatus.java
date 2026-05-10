package com.gong.modu.domain.enums.ipo;

// 공모 이벤트 상태 Enum
// ipo_events.status 컬럼에 들어갈 진행 상태
public enum IpoEventStatus {
    UPCOMING, // 아직 청약이 시작되지 않은 상태
    ONGOING, // 현재 청약 또는 관련 일정이 진행 중인 상태
    CLOSED, // 청약은 종료되었지만 아직 상장 전일 수 있는 상태
    LISTED // 상장까지 완료된 상태
}
