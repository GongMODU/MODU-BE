package com.gong.modu.controller;

import com.gong.modu.domain.dto.DailyCollectSummaryResponse;
import com.gong.modu.domain.dto.VideoDetailSummaryResponse;
import com.gong.modu.domain.dto.VideoSummaryResponse;
import com.gong.modu.service.DailyYouTubeSummaryCollectService;
import com.gong.modu.service.YouTubeSummaryService;
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
            description = "YouTube 자막 추출과 LLM 요약을 수행한 뒤 DB에 저장합니다. 하루 1회 수동 실행 용도입니다." +
                    "프론트에서는 호출하면 안됩니다!!"
    )
    @PostMapping("/admin/summaries/collect")
    public ResponseEntity<DailyCollectSummaryResponse> collectDailySummaries() {
        DailyCollectSummaryResponse response =
                dailyYouTubeSummaryCollectService.collectDailySummaries();
        return ResponseEntity.ok(response);
    }
}
