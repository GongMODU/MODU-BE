package com.gong.modu.scheduler;

import com.gong.modu.domain.entity.ipo.Company;
import com.gong.modu.domain.entity.ipo.IpoDisclosureReport;
import com.gong.modu.repository.ipo.CompanyRepository;
import com.gong.modu.repository.ipo.IpoDisclosureReportRepository;
import com.gong.modu.service.ipo.IpoDisclosureReportSummarizeService;
import com.gong.modu.service.pipeline.KisStockPriceSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j // 배치 시작/종료/실패 로그를 위함
@Component
@RequiredArgsConstructor
// 외부 API 데이터 동기화를 주기적으로 실행하는 스케줄러 클래스
public class ExternalDataScheduler {
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final CompanyRepository companyRepository;
    private final KisStockPriceSyncService kisStockPriceSyncService;
    private final IpoDisclosureReportRepository disclosureReportRepository;
    private final IpoDisclosureReportSummarizeService summarizeService;


    // 상장 기업들의 KIS 주가 뎅ㅣ터를 동기화하는 스케줄러 메서드
    // 주식 장 마감 후 데이터를 가져오기 위해 오후 6시로 설정
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void syncListedCompanyPrices() {
        log.info("[Scheduler] KIS 상장 기업 주가 동기화 시작"); // 배치 시작 로그

        List<Company> companies = companyRepository.findByStockCodeIsNotNull();
        LocalDate today = LocalDate.now();

        String endDate = today.format(BASIC_DATE); // 오늘 날짜를 yyyyMMdd 문자열로 변환
        String startDate = today.minusDays(7).format(BASIC_DATE); // 오늘로부터 7일 전 날짜 변환 -> 최근 7일치 일봉 데이터 보강

        for (Company company : companies) {
            try { // 기업별 try
                kisStockPriceSyncService.syncCurrentPrice(company.getId()); // 해당 기업의 현재가 동기화
                kisStockPriceSyncService.syncDailyPrices(company.getId(), startDate, endDate); // 해당 기업의 최근 7일 일봉 데이터 동기화
            } catch (Exception e) { // 특정 기업의 주가 동기화에 실패했을 경우
                // 어떤 기업에서 실패했는지 companyId와 stockCode를 로그로 남김
                log.warn(
                        "[Scheduler] 주가 동기화 실패: companyId={}, stockCode={}",
                        company.getId(),
                        company.getStockCode(),
                        e // stack trace 출력
                );
            }
        }

        log.info("[Scheduler] KIS 상장 기업 주가 동기화 종료"); // 배치 종료 로그
    }

    // AI 요약이 생성되지 않은 공모주 공시 레코드를 대상으로 Claude API를 호출해 요약을 생성하는 스케줄러
    // 매일 오전 7시 실행, 레코드 간 1초 간격으로 Claude API 과부하 방지
    @Scheduled(cron = "0 0 7 * * *")
    public void summarizeIpoDisclosureReports() {
        log.info("[Scheduler] 공모주 AI 요약 배치 시작");

        List<IpoDisclosureReport> pending = disclosureReportRepository.findByCompanySummaryIsNull();
        log.info("[Scheduler] 요약 대상: {}건", pending.size());

        int success = 0;
        int fail = 0;

        for (IpoDisclosureReport report : pending) {
            try {
                summarizeService.summarize(report.getId());
                success++;
            } catch (Exception e) {
                log.warn("[Scheduler] 요약 실패 reportId={}: {}", report.getId(), e.getMessage());
                fail++;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[Scheduler] 공모주 AI 요약 배치 인터럽트 발생, 배치 중단");
                break;
            }
        }

        log.info("[Scheduler] 공모주 AI 요약 배치 종료 - 성공: {}건, 실패: {}건", success, fail);
    }
}
