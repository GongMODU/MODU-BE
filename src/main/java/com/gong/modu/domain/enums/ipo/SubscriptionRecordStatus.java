package com.gong.modu.domain.enums.ipo;

// 사용자의 청약 이력 상태를 표현하는 Enum
public enum SubscriptionRecordStatus {
    // 청약은 진행했지만 아직 상장, 매도, 최종 손익 정리가 끝나지 않은 상태
    ONGOING,

    // 배정 결과, 매도 결과, 손익 정리까지 완료된 상태
    COMPLETED
}
