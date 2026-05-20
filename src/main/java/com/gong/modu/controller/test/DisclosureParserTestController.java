package com.gong.modu.controller.test;

import com.gong.modu.client.DartApiClient;
import com.gong.modu.domain.dto.pipeline.IpoDisclosureParsingResult;
import com.gong.modu.domain.dto.test.DisclosureParserTestResponse;
import com.gong.modu.domain.dto.test.DisclosureTextParseTestRequest;
import com.gong.modu.service.pipeline.DartDisclosureParsingService;
import com.gong.modu.service.pipeline.DisclosureTextExtractor;
import com.gong.modu.service.pipeline.IpoDisclosureDocumentClassifier;
import com.gong.modu.service.pipeline.IpoDisclosureTextParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile({"local", "local-test"})
@RestController
@RequestMapping("/api/test/disclosure-parser")
@RequiredArgsConstructor
public class DisclosureParserTestController {

    private final DartApiClient dartApiClient;
    private final DisclosureTextExtractor disclosureTextExtractor;
    private final IpoDisclosureTextParser ipoDisclosureTextParser;
    private final IpoDisclosureDocumentClassifier ipoDisclosureDocumentClassifier;
    private final DartDisclosureParsingService dartDisclosureParsingService;

    // 원문 텍스트만 직접 넣어서 parser 결과를 확인하는 테스트 API
    // 사용 목적
    // - 정규식이 수요예측일, 공모가, 기관경쟁률 등을 제대로 잡는지 빠르게 확인
    // - 실제 ZIP 다운로드 없이 parser만 단독 검증
    @PostMapping("/parse-text")
    public ResponseEntity<DisclosureParserTestResponse> parseText(
            @RequestBody @Valid DisclosureTextParseTestRequest request
    ) {
        String originalText = request.getText();

        IpoDisclosureParsingResult parsingResult = ipoDisclosureTextParser.parse(originalText);

        return ResponseEntity.ok(
                DisclosureParserTestResponse.builder()
                        .rceptNo(null)
                        .originalTextLength(originalText.length())
                        .originalTextPreview(makePreview(originalText))
                        .ipoDocumentCandidate(ipoDisclosureDocumentClassifier.isIpoCandidate(originalText))
                        .detectedDocumentType(ipoDisclosureDocumentClassifier.detectDocumentType(originalText))
                        .matchedKeywords(ipoDisclosureDocumentClassifier.findMatchedIpoKeywords(originalText))
                        .parsingResult(parsingResult)
                        .build()
        );
    }

    // 실제 DART rceptNo로 ZIP을 다운로드하고, 파일 내부 텍스트 추출과 정규식 파싱 결과를 함께 확인하는 테스트 API
    // 사용 목적
    // - DART 공시서류원본파일 API가 정상 호출되는지 확인
    // - ZIP 내부 텍스트 추출이 정상적으로 되는지 확인
    // - 실제 공시 문서에서 정규식 파싱 결과가 나오는지 확인
    @GetMapping("/dry-run/{rceptNo}")
    public ResponseEntity<DisclosureParserTestResponse> dryRunByRceptNo(
            @PathVariable String rceptNo
    ) {
        byte[] zipBytes = dartApiClient.downloadDisclosureDocumentZip(rceptNo);

        String originalText = disclosureTextExtractor.extractTextFromZip(zipBytes);

        IpoDisclosureParsingResult parsingResult = ipoDisclosureTextParser.parse(originalText);

        return ResponseEntity.ok(
                DisclosureParserTestResponse.builder()
                        .rceptNo(rceptNo)
                        .originalTextLength(originalText.length())
                        .originalTextPreview(makePreview(originalText))
                        .ipoDocumentCandidate(ipoDisclosureDocumentClassifier.isIpoCandidate(originalText))
                        .detectedDocumentType(ipoDisclosureDocumentClassifier.detectDocumentType(originalText))
                        .matchedKeywords(ipoDisclosureDocumentClassifier.findMatchedIpoKeywords(originalText))
                        .parsingResult(parsingResult)
                        .build()
        );
    }

    // 실제 DART rceptNo로 ZIP 다운로드, 텍스트 추출, 파싱, DB 반영까지 수행하는 테스트 API
    // 사용 목적
    // - DartDisclosureParsingService 전체 흐름 검증
    // - 스케줄러가 내부적으로 수행할 작업을 단건으로 먼저 테스트
    @PostMapping("/apply")
    public ResponseEntity<Void> parseAndApply(
            @RequestParam Long ipoEventId,
            @RequestParam String rceptNo
    ) {
        dartDisclosureParsingService.parseDisclosureReport(ipoEventId, rceptNo);

        return ResponseEntity.ok().build();
    }

    // 원문의 앞부분 일부만 잘라서 미리보기로 반환하는 메서드
    private String makePreview(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        int previewLength = Math.min(text.length(), 1000);

        return text.substring(0, previewLength);
    }

}
