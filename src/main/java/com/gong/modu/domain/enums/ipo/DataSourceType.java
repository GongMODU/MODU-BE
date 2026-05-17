package com.gong.modu.domain.enums.ipo;

// 공모주 데이터 출처의 생성 방식을 저장하는 Enum
public enum DataSourceType {

    // 외부 API 응답값을 그대로 저장한 경우
    API,

    // 공시 원문, ZIP 파일, HTML, XML 등을 파싱해서 추출한 경우
    PARSING,

    // 관리자가 직접 입력하거나 수정한 경우
    MANUAL,

    // 내부 계산 로직으로 산출한 경우
    CALCULATED
}
