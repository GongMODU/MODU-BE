package com.gong.modu.domain.dto.dart;

import lombok.Getter;
import lombok.NoArgsConstructor;

// DART 기업개황 API 응답 DTO - /api/company.json
// DART corp_code를 기준으로 기업의 기본 프로필을 가져올 때 사용
@Getter
@NoArgsConstructor
public class DartCompanyResponse {
    private String status; // DART 응답 상태 코드 (정상: 000)
    private String message;
    private String corpCode; // DART 기업 고유번호
    private String corpName;
    private String corpNameEng;
    private String stockName; // 종목명
    private String stockCode; // 종목코드
    private String ceoNm;
    private String corpCls; // 법인 구분 (Y, K, N, E)
    private String jurirNo;
    private String bizrNo;
    private String adres;
    private String hmUrl;
    private String irUrl;
    private String phnNo;
    private String faxNo;
    private String indutyCode;  // 업종 코드
    private String estDt; // 설립일
    private String accMt;
}
