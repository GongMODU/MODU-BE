package com.gong.modu.controller;

import com.gong.modu.domain.dto.ipo.IpoDisclosureReportResponse;
import com.gong.modu.domain.dto.ipo.IpoFinancialResponse;
import com.gong.modu.service.ipo.IpoDisclosureReportQueryService;
import com.gong.modu.service.ipo.IpoFinancialQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ipo")
@RequiredArgsConstructor
public class IpoController {

    private final IpoDisclosureReportQueryService queryService;
    private final IpoFinancialQueryService financialQueryService;

    @GetMapping("/{ipoEventId}/disclosure")
    public ResponseEntity<IpoDisclosureReportResponse> getDisclosureReport(@PathVariable Long ipoEventId) {
        return ResponseEntity.ok(queryService.getDisclosureReport(ipoEventId));
    }

    // 재무 차트용 연간 재무 하이라이트 조회 / 최신 2개년, bsnsYear 오름차순
    // 데이터 없으면 빈 배열 반환 (404 아님)
    @GetMapping("/{ipoEventId}/financials")
    public ResponseEntity<List<IpoFinancialResponse>> getFinancials(@PathVariable Long ipoEventId) {
        return ResponseEntity.ok(financialQueryService.getFinancials(ipoEventId));
    }
}
