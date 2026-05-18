package com.gong.modu.controller;

import com.gong.modu.domain.entity.ipo.IpoDisclosureReport;
import com.gong.modu.repository.ipo.IpoDisclosureReportRepository;
import com.gong.modu.service.ipo.IpoDisclosureReportSummarizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IpoAdminController {

    private final IpoDisclosureReportRepository reportRepository;
    private final IpoDisclosureReportSummarizeService summarizeService;

    // 특정 공모 이벤트의 공시 요약을 즉시 강제 재생성하는 관리자용 API
    // 기존 summary 데이터가 있어도 덮어씀
    @PostMapping("/admin/ipo/{ipoEventId}/summarize")
    public Map<String, String> summarize(@PathVariable Long ipoEventId) {
        List<IpoDisclosureReport> reports = reportRepository.findByIpoEventId(ipoEventId);

        if (reports.isEmpty()) {
            return Map.of("message", "해당 공모 이벤트의 공시 데이터가 없습니다.");
        }

        int success = 0;
        int fail = 0;

        for (IpoDisclosureReport report : reports) {
            try {
                summarizeService.summarize(report.getId());
                success++;
            } catch (Exception e) {
                log.warn("[IpoAdmin] 요약 실패 reportId={}: {}", report.getId(), e.getMessage());
                fail++;
            }
        }

        return Map.of("message", "요약 완료 (성공: " + success + "건, 실패: " + fail + "건)");
    }
}
