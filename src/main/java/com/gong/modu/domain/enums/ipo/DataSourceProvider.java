package com.gong.modu.domain.enums.ipo;

// 외부 데이터 제공자를 구분하는 Enum
public enum DataSourceProvider {

    // 금융감독원 전자공시시스템 DART
    // 기업 고유번호, 공시검색, 증권신고서 주요정보, 재무제표 수집에 사용
    DART,

    // 한국투자증권 KIS
    // 현재가, 기간별 시세 등 주가 데이터 수집에 사용
    KIS,

    // 한국거래소 KRX 데이터
    // 종목 기본정보, 일별 매매정보 보강에 사용
    KRX,

    // 외부 API가 아니라 서비스 내부 관리자 입력 또는 내부 계산인 경우
    INTERNAL
}
