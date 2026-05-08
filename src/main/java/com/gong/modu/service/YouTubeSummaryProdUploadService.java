package com.gong.modu.service;

import com.gong.modu.domain.dto.AdminYouTubeSummaryReplaceRequest;
import com.gong.modu.domain.dto.AdminYouTubeSummaryReplaceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

// 로컬 DB의 export 결과를 운영 서버 replace API로 전송하는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryProdUploadService {

    private final YouTubeSummaryAdminService youTubeSummaryAdminService;
    private final WebClient webClient = WebClient.builder().build();

    // 운영 서버 replace API 주소
    @Value("${youtube.prod.replace-url}")
    private String prodReplaceUrl;

    // 운영 서버 관리자 API 키
    @Value("${admin.youtube.key}")
    private String prodAdminKey;

    public AdminYouTubeSummaryReplaceResponse uploadExportResultToProd() {
        // 현재 로컬 DB 내용을 export DTO 형태로 변환
        List<AdminYouTubeSummaryReplaceRequest> exportResult = youTubeSummaryAdminService.buildReplaceRequests();

        if (exportResult.isEmpty()) {
            throw new IllegalStateException("운영 서버로 업로드할 YouTube 요약 데이터가 없습니다.");
        }

        log.info("운영 서버 YouTube 요약 업로드 시작. count={}, url={}",
                exportResult.size(),
                prodReplaceUrl);

        // export 결과를 그대로 운영 서버 replace API의 request body로 전송
        AdminYouTubeSummaryReplaceResponse response = webClient.post()
                .uri(prodReplaceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ADMIN-KEY", prodAdminKey)
                .bodyValue(exportResult)
                .retrieve()
                .bodyToMono(AdminYouTubeSummaryReplaceResponse.class)
                .block();

        log.info("운영 서버 YouTube 요약 업로드 완료. response={}", response);

        return response;
    }
}
