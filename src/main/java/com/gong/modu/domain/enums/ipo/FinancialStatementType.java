package com.gong.modu.domain.enums.ipo;

// DART 재무제표에서 연결/개별 재무제표 구분을 표현하는 Enum
public enum FinancialStatementType {
    // 연결 재무제표
    // 종속회사를 포함한 그룹 전체 기준 재무제표
    CFS,

    // 개별 재무제표
    // 해당 회사 단독 기준 재무제표
    OFS
}
