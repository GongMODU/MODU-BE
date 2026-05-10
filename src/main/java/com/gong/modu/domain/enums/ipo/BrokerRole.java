package com.gong.modu.domain.enums.ipo;

// 공모주 청약에 참여하는 증권사의 역할을 표현하는 Enum
public enum BrokerRole {
    // 대표주관사
    LEAD_MANAGER,

    // 공동주관사
    CO_MANAGER,

    // 인수회사
    // 공모 주식을 일정 수량 인수하거나 판매를 담당하는 증권사
    UNDERWRITER,

    // 기타
    ETC
}
