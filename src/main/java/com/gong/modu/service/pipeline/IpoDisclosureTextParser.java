package com.gong.modu.service.pipeline;

import com.gong.modu.domain.dto.IpoDisclosureParsingResult;
import com.gong.modu.util.ExternalDateParser;
import com.gong.modu.util.ExternalNumberParser;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
// 공시 원문 텍스트에서 IPO 핵심값을 정규식 기반으로 추출하는 클래스
// 텍스트에서 자주 등장하는 패턴을 우선 추출하는 1차 파서
public class IpoDisclosureTextParser {

    // 원문 텍스트를 받아 파싱 결과 DTO로 변환하는 메서드
    public IpoDisclosureParsingResult parse(String text) {
        if (text == null || text.isBlank()) {
            return IpoDisclosureParsingResult.builder().build();
        }

        String normalized = normalize(text);

        // 수요예측일 범위 추출
        DateRange demandForecastRange = findDateRangeNearKeyword(normalized, "수요예측");

        // 환불일 추출
        LocalDate refundDate = findDateNearKeyword(normalized, "상장일|상장예정일|매매개시일");

        // 상장일 추출
        LocalDate listingDate = findDateNearKeyword(normalized, "환불일|환불기일|청약증거금.*환불|납입 및 환불");

        // 보호예수 해제일 또는 의무보유 해제일 추출
        LocalDate lockupExpiryDate = findDateNearKeyword(
                normalized,
                "보호예수.*해제|의무보유.*해제|매각제한.*해제"
        );

        // 희망공모가 범위 추출
        PriceRange offerPriceRange = findPriceRangeNearKeyword(
                normalized,
                "희망공모가|공모희망가|희망 공모가액"
        );

        // 확정공모가 추출
        BigDecimal offerPrice = findMoneyNearKeyword(
                normalized,
                "확정공모가|확정 공모가|공모가액 확정|발행가액"
        );

        // 공모주식수 추출
        Long shareCount = findShareCountNearKeyword(
                normalized,
                "공모주식수|모집주식수|공모 주식수|모집 주식수"
        );

        // 상장주식수 추출
        Long totalListedShares = findShareCountNearKeyword(
                normalized,
                "상장예정주식수|상장주식수|상장 예정 주식수"
        );

        // 기관경쟁률 추출
        BigDecimal institutionalCompetitionRate = findRateNearKeyword(
                normalized,
                "기관경쟁률|기관투자자.*경쟁률|수요예측.*경쟁률"
        );

        // 의무보유확약 비율 추출
        BigDecimal lockupRatio = findPercentRatioNearKeyword(
                normalized,
                "의무보유확약|확약비율|의무보유 확약"
        );

        // 보호예수 비율 추출
        BigDecimal protectiveCustodyRatio = findPercentRatioNearKeyword(
                normalized,
                "보호예수|매각제한|의무보유"
        );

        // 추출된 값들을 결과 DTO에 담아 반환
        return IpoDisclosureParsingResult.builder()
                .demandForecastStart(demandForecastRange.start())
                .demandForecastEnd(demandForecastRange.end())
                .refundDate(refundDate)
                .listingDate(listingDate)
                .lockupExpiryDate(lockupExpiryDate)
                .offerPriceMin(offerPriceRange.min())
                .offerPriceMax(offerPriceRange.max())
                .offerPrice(offerPrice)
                .shareCount(shareCount)
                .totalListedShares(totalListedShares)
                .institutionalCompetitionRate(institutionalCompetitionRate)
                .lockupRatio(lockupRatio)
                .protectiveCustodyRatio(protectiveCustodyRatio)
                .build();
    }

    // 원문 공백을 정규화하는 메서드
    private String normalize(String text) {
        return text
                .replaceAll("\\s+", " ")
                .trim();
    }



    // 특정 키워드 근처에서 날짜 범위를 찾는 메서드
    private DateRange findDateRangeNearKeyword(String text, String keywordRegex) {

        // 키워드 뒤쪽 일정 범위 안에서 날짜 두 개를 찾는 정규식
        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?(\\d{4}[.\\-/년 ]+\\d{1,2}[.\\-/월 ]+\\d{1,2}).{0,40}?(\\d{4}[.\\-/년 ]+\\d{1,2}[.\\-/월 ]+\\d{1,2})"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        // 매칭되는 첫 번째 구간이 있으면 시작일과 종료일을 추출
        if (matcher.find()) {

            LocalDate start = ExternalDateParser.parseFlexibleDate(matcher.group(2));
            LocalDate end = ExternalDateParser.parseFlexibleDate(matcher.group(3));

            return new DateRange(start, end);

        }

        // 날짜 범위를 찾지 못하면 null 값으로 된 DateRange를 반환
        return new DateRange(null, null);

    }

    // 특정 키워드 근처에서 날짜 하나를 찾는 메서드
    private LocalDate findDateNearKeyword(String text, String keywordRegex) {

        // 키워드 뒤쪽 120자 이내에서 날짜처럼 보이는 패턴을 찾음
        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?(\\d{4}[.\\-/년 ]+\\d{1,2}[.\\-/월 ]+\\d{1,2})"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        // 매칭되면 두 번째 그룹의 날짜 문자열을 LocalDate로 변환
        if (matcher.find()) {
            return ExternalDateParser.parseFlexibleDate(matcher.group(2));
        }

        // 찾지 못하면 null을 반환
        return null;

    }

    // 특정 키워드 근처에서 금액 범위를 찾는 메서드

    private PriceRange findPriceRangeNearKeyword(String text, String keywordRegex) {

        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?([0-9,]+)\\s*원?.{0,20}?~.{0,20}?([0-9,]+)\\s*원?"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        // 매칭되면 최소/최대 가격을 BigDecimal로 변환
        if (matcher.find()) {

            BigDecimal min = ExternalNumberParser.toBigDecimal(matcher.group(2));
            BigDecimal max = ExternalNumberParser.toBigDecimal(matcher.group(3));

            return new PriceRange(min, max);
        }

        // 찾지 못하면 null 범위를 반환
        return new PriceRange(null, null);

    }

    // 특정 키워드 근처에서 단일 금액을 찾는 메서드
    private BigDecimal findMoneyNearKeyword(String text, String keywordRegex) {


        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?([0-9,]+)\\s*원"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return ExternalNumberParser.toBigDecimal(matcher.group(2));
        }

        // 찾지 못하면 null을 반환
        return null;
    }

    // 특정 키워드 근처에서 주식 수를 찾는 메서드
    private Long findShareCountNearKeyword(String text, String keywordRegex) {

        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?([0-9,]+)\\s*주"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        // 매칭되면 주식 수 문자열을 Long으로 변환
        if (matcher.find()) {
            return ExternalNumberParser.toLong(matcher.group(2));
        }

        // 찾지 못하면 null을 반환
        return null;

    }

    // 특정 키워드 근처에서 경쟁률을 찾는 메서드
    private BigDecimal findRateNearKeyword(String text, String keywordRegex) {

        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,120}?([0-9,]+(?:\\.\\d+)?)\\s*[:대]?\\s*1"
        );

        // 정규식을 text에 적용
        Matcher matcher = pattern.matcher(text);

        // 매칭되면 경쟁률 숫자 부분만 BigDecimal로 변환.
        if (matcher.find()) {
            return ExternalNumberParser.toBigDecimal(matcher.group(2));
        }

        // 찾지 못하면 null을 반환
        return null;

    }

    // 특정 키워드 근처에서 퍼센트를 찾아 0~1 사이 비율로 변환하는 메서드
    private BigDecimal findPercentRatioNearKeyword(String text, String keywordRegex) {

        Pattern pattern = Pattern.compile(
                "(" + keywordRegex + ").{0,160}?([0-9,]+(?:\\.\\d+)?)\\s*%"
        );

        // 정규식을 text에 적용합니다.
        Matcher matcher = pattern.matcher(text);

        // 매칭되지 않으면 null을 반환합
        if (!matcher.find()) {
            return null;
        }

        BigDecimal percent = ExternalNumberParser.toBigDecimal(matcher.group(2));

        if (percent == null) {
            return null;
        }

        // 15.3%를 0.1530처럼 DB 저장용 비율로 변경
        return percent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    // 날짜 범위를 표현하는 내부 record
    private record DateRange(LocalDate start, LocalDate end) {
    }

    // 가격 범위를 표현하는 내부 record
    // 희망공모가 하단/상단처럼 두 값을 함께 다룰 때 사용
    private record PriceRange(BigDecimal min, BigDecimal max) {
    }
}
