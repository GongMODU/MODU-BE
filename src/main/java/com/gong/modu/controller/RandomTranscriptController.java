package com.gong.modu.controller;

import com.gong.modu.domain.dto.RandomTranscriptResponse;
import com.gong.modu.service.RandomTranscriptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 랜덤 YouTube 자막 추출 기능을 검증하기 위한 Controller
// 검증용이므로 스웨거 사용 X
@RestController
@RequestMapping("/api/youtube")
public class RandomTranscriptController {

    private final RandomTranscriptService randomTranscriptService;

    public RandomTranscriptController(RandomTranscriptService randomTranscriptService) {
        this.randomTranscriptService = randomTranscriptService;
    }

    // 사전에 등록된 유튜브 채널들의 최신 영상 중 랜덤한 영상을 선택하고 해당 영상의 텍스트를 반환함
    // GET /api/youtube/random-transcript
    @GetMapping("/random-transcript")
    public ResponseEntity<RandomTranscriptResponse> getRandomTranscript() {
        RandomTranscriptResponse response = randomTranscriptService.getRandomTranscript();
        return ResponseEntity.ok(response);
    }
}
