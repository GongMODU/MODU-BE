package com.gong.modu.domain.dto.dart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// DART 단일회사 전체 재무제표 API 응답 DTO - /api/fnlttSinglAcntAll.json
// 기업 탭에서 사용할 재무 하이라이트를 만들기 위한 원천 응답
// API 응답에는 계정과목이 여러 줄로 내려오므로 이후 Pipeline 과정에서 매출액, 영업이익, 순이익, 자산총계, 부채총계, 자본총계만 골라 CompanyFinancialHighlight 엔티티로 변환
@Getter
@NoArgsConstructor
public class DartFinancialStatementResponse {
    private String status; // DART 응답 상태 코드 (정상: 000)
    private String message;
    private List<Item> list; // 재무제표 계정 항목 목록 (각 item은 재무제포 한 계정과목 row)

    @Getter
    @NoArgsConstructor
    public static class Item {
        @JsonProperty("rcept_no")
        private String rceptNo; // 공시 접수번호

        @JsonProperty("reprt_code")
        private String reprtCode; // 보고서 코드 (11011: 사업보고서, 11012: 반기보고서, 11013: 1분기보고서, 11014: 3분기보고서)

        @JsonProperty("bsns_year")
        private String bsnsYear; // 사업연도

        @JsonProperty("corp_code")
        private String corpCode; // DART 기업 고유번호입

        @JsonProperty("sj_div")
        private String sjDiv; // 재무제표 구분 코드

        @JsonProperty("sj_nm")
        private String sjNm; // 재무제표 구분명

        @JsonProperty("account_id")
        private String accountId; // 계정 ID

        @JsonProperty("account_nm")
        private String accountNm; // 계정명

        @JsonProperty("account_detail")
        private String accountDetail; // 계정 상세

        @JsonProperty("thstrm_nm")
        private String thstrmNm; // 당기명

        @JsonProperty("thstrm_amount")
        private String thstrmAmount; // 당기 금액

        @JsonProperty("frmtrm_nm")
        private String frmtrmNm; // 전기명

        @JsonProperty("frmtrm_amount")
        private String frmtrmAmount; // 전기 금액

        @JsonProperty("bfefrmtrm_nm")
        private String bfefrmtrmNm; // 전전기명

        @JsonProperty("bfefrmtrm_amount")
        private String bfefrmtrmAmount; // 전전기 금액

        private String ord; // 정렬 순서
        private String currency; // 통화 단위
    }
}
