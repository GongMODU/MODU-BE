package com.gong.modu.domain.dto.test;

import com.gong.modu.domain.dto.pipeline.IpoDisclosureParsingResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DisclosureParserTestResponse {

    // 파싱 대상 공시 접수번호
    private String rceptNo;

    // 원문 텍스트 길이
    private int originalTextLength;

    // 원문 일부 미리보기
    private String originalTextPreview;

    // 이 문서가 IPO/공모주 parser 대상 문서로 보이는지 여부
    private boolean ipoDocumentCandidate;

    // 원문에서 감지한 문서 유형 또는 대표 제목
    private String detectedDocumentType;

    // 원문에서 발견한 IPO 관련 키워드 목록
    private List<String> matchedKeywords;

    // 실제 정규식 파서가 추출한 결과
    private IpoDisclosureParsingResult parsingResult;
}
