package com.gong.modu.domain.enums.ipo;

// 공모 이벤트 도메인 Enum
// ipo_events.event_type 컬럼에 들어갈 이벤트 유형
public enum IpoEventType {
    IPO, // 일반적인 신규 상장 공모주 이벤트
    GENERAL_OFFER, // 일반 공모
    PAID_IN_CAPITAL_INCREASE, // 유상증자
    THIRD_PARTY_ALLOTMENT // 제3자 배정 방식
}
