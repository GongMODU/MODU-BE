package com.gong.modu.domain.enums.ipo;

// DART 단일회사 전체 재무제표 API에서 사용하는 보고서 코드 Enum
public enum ReportCode {
    // 사업보고서
    // 보통 1년 전체 재무제표를 확인할 때 사용
    ANNUAL("11011"),

    // 반기보고서
    // 1년 중 상반기 기준 재무제표를 확인할 때 사용
    HALF_YEAR("11012"),

    // 1분기보고서
    // 1분기 기준 재무제표를 확인할 때 사용
    FIRST_QUARTER("11013"),

    // 3분기보고서
    // 3분기 기준 재무제표를 확인할 때 사용
    THIRD_QUARTER("11014");

    // DART API에서 실제로 사용하는 문자열 코드
    private final String code;

    // Enum 상수마다 DART 코드값을 연결하기 위한 생성자
    ReportCode(String code) {
        this.code = code;
    }

    // 외부 API 요청이나 응답 비교 시 실제 코드값을 꺼내기 위한 메서드
    public String getCode() {
        return code;
    }
}
