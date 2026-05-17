package com.gong.modu.domain.dto.dart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// DART 기업개황 API 응답 DTO - /api/company.json
// DART corp_code를 기준으로 기업의 기본 프로필을 가져올 때 사용
@Getter
@NoArgsConstructor
public class DartCompanyResponse {
    private String status; // DART 응답 상태 코드 (정상: 000)
    private String message;

    @JsonProperty("corp_code")
    private String corpCode; // DART 기업 고유번호

    @JsonProperty("corp_name")
    private String corpName;

    private String corpNameEng;

    @JsonProperty("stock_name")
    private String stockName; // 종목명

    @JsonProperty("stock_code")
    private String stockCode; // 종목코드

    @JsonProperty("ceo_nm")
    private String ceoNm;

    @JsonProperty("corp_cls")
    private String corpCls; // 법인 구분 (Y, K, N, E)

    @JsonProperty("jurir_no")
    private String jurirNo;

    @JsonProperty("bizr_no")
    private String bizrNo;

    @JsonProperty("adres")
    private String adres;

    @JsonProperty("hm_url")
    private String hmUrl;

    @JsonProperty("ir_url")
    private String irUrl;

    @JsonProperty("phn_no")
    private String phnNo;

    @JsonProperty("fax_no")
    private String faxNo;

    @JsonProperty("induty_code")
    private String indutyCode;  // 업종 코드

    @JsonProperty("est_dt")
    private String estDt; // 설립일

    @JsonProperty("acc_mt")
    private String accMt;
}
