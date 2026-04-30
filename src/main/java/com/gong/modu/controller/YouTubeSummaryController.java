package com.gong.modu.controller;

import com.gong.modu.domain.dto.VideoDetailSummaryResponse;
import com.gong.modu.domain.dto.VideoSummaryResponse;
import com.gong.modu.service.YouTubeSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "YouTube Summary", description = "유튜브 영상 요약 API")
@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YouTubeSummaryController {

    private final YouTubeSummaryService youtubeSummaryService;

    @Operation(summary = "홈 화면용 3줄 요약", description = "랜덤 유튜브 영상 최대 3개의 3줄 요약을 반환합니다.")
    @GetMapping("/summary")
    public ResponseEntity<List<VideoSummaryResponse>> getSummaries() {
        return ResponseEntity.ok(youtubeSummaryService.getSummaries());
    }

    @Operation(summary = "상세 모달용 긴 설명", description = "videoId에 해당하는 유튜브 영상의 상세 설명을 반환합니다.")
    @GetMapping("/summary/detail")
    public ResponseEntity<VideoDetailSummaryResponse> getDetailSummary(@RequestParam String videoId) {
        return ResponseEntity.ok(youtubeSummaryService.getDetailSummary(videoId));
    }
}
