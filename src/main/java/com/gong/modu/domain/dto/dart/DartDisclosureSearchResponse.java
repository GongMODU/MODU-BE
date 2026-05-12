package com.gong.modu.domain.dto.dart;

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
    private Integer pageNo; // 현재 페이지 번호
    private Integer pageCount; // 한 페이지당 조회 개수
    private Integer totalCount; // 전체 검색 결과 개수
    private Integer totalPage; // 전체 페이지 수
    private List<Item> list;  // 실제 공시 목록 (각 item 한 건이 공시 한 건)

    // 공시검색 결과의 개별 공시 항목 DTO
    @Getter
    @NoArgsConstructor
    public static class Item {
        private String corpCode; // DART 기업 고유번호
        private String corpName; // 기업명
        private String stockCode; // 종목코드 (상장사가 아닌 경우 null)
        private String corpCls; // 법인 구분 (Y: 유가증권, K: 코스닥, N: 코넥스, E: 기타)
        private String reportNm; // 공시 보고서명
        private String rceptNo; // DART 공시 접수번호
        private String flrNm; // 제출인명
        private String rceptDt; // 접수일자
        private String rm; // 비고
    }
}
