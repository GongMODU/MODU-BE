package com.gong.modu.controller;

import com.gong.modu.domain.dto.youtube.*;
import com.gong.modu.service.youtube.DailyYouTubeSummaryCollectService;
import com.gong.modu.service.youtube.YouTubeSummaryAdminService;
import com.gong.modu.service.youtube.YouTubeSummaryProdUploadService;
import com.gong.modu.service.youtube.YouTubeSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "YouTube Summary", description = "유튜브 영상 요약 API")
@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YouTubeSummaryController {

    private final YouTubeSummaryService youtubeSummaryService;
    private final DailyYouTubeSummaryCollectService dailyYouTubeSummaryCollectService;
    private final YouTubeSummaryAdminService youTubeSummaryAdminService;
    private final YouTubeSummaryProdUploadService youTubeSummaryProdUploadService;

    @Operation(summary = "홈 화면용 3줄 요약", description = "유튜브 영상 요약 랜덤 최대 3개를 반환합니다.")
    @GetMapping("/summary")
    public ResponseEntity<List<VideoSummaryResponse>> getSummaries() {
        return ResponseEntity.ok(youtubeSummaryService.getSummaries());
    }

    @Operation(summary = "상세 모달용 긴 설명", description = "videoId에 해당하는 유튜브 영상의 상세 요약을 반환합니다.")
    @GetMapping("/summary/detail")
    public ResponseEntity<VideoDetailSummaryResponse> getDetailSummary(@RequestParam String videoId) {
        return ResponseEntity.ok(youtubeSummaryService.getDetailSummary(videoId));
    }

    @Operation(
            summary = "관리자용 유튜브 요약 수동 수집",
            description = "YouTube 자막 추출과 LLM 요약을 수행한 뒤 DB에 저장합니다. 하루 1회 수동 실행 용도입니다. " +
                    "백엔드 내부 호출용입니다 !!!"
    )
    @PostMapping("/admin/summaries/collect")
    public ResponseEntity<DailyCollectSummaryResponse> collectDailySummaries() {
        DailyCollectSummaryResponse response =
                dailyYouTubeSummaryCollectService.collectDailySummaries();
        return ResponseEntity.ok(response);
    }

    // 로컬 export 결과를 운영 서버 replace API의 body로 자동 전송
    @Operation(
            summary = "관리자용 유튜브 요약 데이터 교체",
            description = "로컬에서 생성한 YouTube 요약 JSON을 받아 기존 요약 데이터를 통째로 교체합니다. EC2에서 자막 추출을 수행하지 않는 용도입니다."
                    + " 백엔드 내부 호출용입니다!!!"
    )
    @PostMapping("/admin/summaries/upload-to-prod")
    public ResponseEntity<AdminYouTubeSummaryReplaceResponse> uploadSummariesToProd() {
        AdminYouTubeSummaryReplaceResponse response =
                youTubeSummaryProdUploadService.uploadExportResultToProd();
        return ResponseEntity.ok(response);

    }

    // 운영 서버에서 JSON body를 받아 기존 요약 데이터를 교체
    @PostMapping("/admin/summaries/replace")
    public ResponseEntity<AdminYouTubeSummaryReplaceResponse> replaceSummaries(
            @RequestBody List<AdminYouTubeSummaryReplaceRequest> requests
    ) {
        return ResponseEntity.ok(
                youTubeSummaryAdminService.replaceSummaries(requests)
        );
    }
}
