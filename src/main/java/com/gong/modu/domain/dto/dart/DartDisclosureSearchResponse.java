package com.gong.modu.domain.dto.dart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// DART 공시검색 API 응답 DTO - /api/list.json
// 목적: 공모 관련 공시 검색, rcept_no 확보, 공시문서 후보 탐색
@Getter
@NoArgsConstructor
public class DartDisclosureSearchResponse {

    private String status; // DART 응답 상태 코드 (정상: 000)
    private String message;

    @JsonProperty("page_no")
    private Integer pageNo; // 현재 페이지 번호

    @JsonProperty("page_count")
    private Integer pageCount; // 한 페이지당 조회 개수

    @JsonProperty("total_count")
    private Integer totalCount; // 전체 검색 결과 개수

    @JsonProperty("total_page")
    private Integer totalPage; // 전체 페이지 수

    private List<Item> list;  // 실제 공시 목록 (각 item 한 건이 공시 한 건)

    // 공시검색 결과의 개별 공시 항목 DTO
    @Getter
    @NoArgsConstructor
    public static class Item {
        @JsonProperty("corp_code")
        private String corpCode; // DART 기업 고유번호

        @JsonProperty("corp_name")
        private String corpName; // 기업명

        @JsonProperty("stock_code")
        private String stockCode; // 종목코드 (상장사가 아닌 경우 null)

        @JsonProperty("corp_cls")
        private String corpCls; // 법인 구분 (Y: 유가증권, K: 코스닥, N: 코넥스, E: 기타)

        @JsonProperty("report_nm")
        private String reportNm; // 공시 보고서명

        @JsonProperty("rcept_no")
        private String rceptNo; // DART 공시 접수번호

        @JsonProperty("flr_nm")
        private String flrNm; // 제출인명

        @JsonProperty("rcept_dt")
        private String rceptDt; // 접수일자

        private String rm; // 비고
    }
}
