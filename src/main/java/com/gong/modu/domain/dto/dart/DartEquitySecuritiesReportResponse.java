package com.gong.modu.domain.dto.dart;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// DART 지분증권 증권신고서 주요정보 API 응답 DTO - /api/estkRs.json
// 공모주 일정과 공모 조건 일부를 가져오기 위해 사용 (청약일, 납입일, 청약공고일, 배정공고일, 인수기관명 등)
// 단 기관경쟁률, 의무보유확약, 비례경쟁률 등은 이 API에서 항상 완전하게 제공되지 않으므로 이후 원문 파싱 또는 수동 입력으로 보완 필요
@Getter
@NoArgsConstructor
public class DartEquitySecuritiesReportResponse {

    private String status; // DART 응답 상태 코드 (정상: 000)
    private String message;
    private List<Item> list;  // 지분증권 증권신고서 주요정보 목록

    @Getter
    @NoArgsConstructor
    public static class Item {

        private String rceptNo; // 공시 접수번호
        private String corpCls; // 법인 구분 (Y/K/N/E)
        private String corpCode; // DART 기업 고유번호
        private String corpName; // 기업명
        private String sbd; // 청약기일
        private String pymd; // 납입기일
        private String sband; // 청약공고일
        private String asand; // 배정공고일
        private String asstd; // 배정기준일
        private String udtintnm; // 인수기관명
        private String title; // 그룹명칭
    }
}
