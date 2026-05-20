package com.gong.modu.service.pipeline;

import com.gong.modu.domain.dto.pipeline.IpoDisclosureParsingResult;
import com.gong.modu.util.ExternalDateParser;
import com.gong.modu.util.ExternalNumberParser;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
// 공시 원문 텍스트에서 IPO 핵심값을 정규식 기반으로 추출하는 클래스
// 공시 문서는 같은 키워드와 숫자가 여러 번 반복되므로, 단순히 첫 번째 숫자를 잡지 않고 필드별 우선순위를 적용함
public class IpoDisclosureTextParser {

    // 날짜 패턴
    // 예: 2021.07.26, 2021-07-26, 2021년 07월 26일 같은 형태를 잡기 위한 정규식
    private static final String DATE_REGEX =
            "\\d{4}\\s*[.\\-/년]\\s*\\d{1,2}\\s*[.\\-/월]\\s*\\d{1,2}\\s*(?:일)?";

    // 금액/수량 숫자 패턴
    private static final String NUMBER_REGEX =
            "[0-9]{1,3}(?:,[0-9]{3})+|[0-9]+";

    // 원문 텍스트를 받아 파싱 결과 DTO로 변환하는 메서드
    public IpoDisclosureParsingResult parse(String text) {

        // 원문이 없으면 모든 필드가 null인 결과를 반환
        if (text == null || text.isBlank()) {
            return IpoDisclosureParsingResult.builder().build();
        }

        String normalized = normalize(text);

        // 수요예측일 범위 추출
        DateRange demandForecastRange = findDateRangeByKeywords(
                normalized,
                List.of("수요예측일", "수요예측 기간", "수요예측")
        );

        // 환불일 추출
        LocalDate refundDate = findDateByKeywords(
                normalized,
                List.of("환불일", "환불기일", "청약증거금 환불", "납입 및 환불")
        );

        // 상장일 추출
        LocalDate listingDate = findDateByKeywords(
                normalized,
                List.of("상장일", "상장예정일", "상장 예정일", "매매개시일", "매매 개시일")
        );

        // 락업해제일 추출
        LocalDate lockupExpiryDate = findDateByKeywords(
                normalized,
                List.of("보호예수 해제", "의무보유 해제", "매각제한 해제", "보호예수기간 만료", "의무보유기간 만료")
        );

        // 희망공모가 범위 추출
        PriceRange offerPriceRange = findPriceRangeByKeywords(
                normalized,
                List.of("희망공모가", "희망 공모가", "공모희망가", "공모 희망가", "모집(매출)가액(예정)")
        );

        // 확정공모가 추출
        BigDecimal offerPrice = findMoneyByKeywords(
                normalized,
                List.of("확정공모가", "확정 공모가", "공모가액 확정", "모집(매출) 확정가액", "발행가액")
        );

        // 공모주식수 추출
        // 단순히 "주"가 붙은 숫자를 잡으면 우리사주조합 배정수량 등이 잡힐 수 있으므로 강한 키워드를 우선
        Long shareCount = findShareCountByPriority(normalized);

        // 상장예정주식수 추출
        Long totalListedShares = findShareCountByKeywords(
                normalized,
                List.of("상장예정주식수", "상장 예정 주식수", "상장주식수", "상장 주식수")
        );

        // 기관경쟁률 추출
        // "37.92%" 같은 비율이 경쟁률로 잘못 잡히지 않도록 ":1", "대 1" 형태를 우선
        BigDecimal institutionalCompetitionRate = findCompetitionRateByKeywords(
                normalized,
                List.of("기관경쟁률", "기관투자자 경쟁률", "수요예측 경쟁률", "수요예측 결과", "경쟁률")
        );

        // 의무보유확약 비율 추출
        BigDecimal lockupRatio = findPercentRatioByKeywords(
                normalized,
                List.of("의무보유확약 비율", "의무보유 확약 비율", "의무보유확약", "확약비율")
        );

        // 락업 비율 추출
        BigDecimal protectiveCustodyRatio = findPercentRatioByKeywords(
                normalized,
                List.of("보호예수 비율", "보호예수", "매각제한 비율", "매각제한")
        );

        // 추출된 값들을 DTO에 담아 반환
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

    // 공시 원문 공백을 정리하는 메서드
    private String normalize(String text) {
        return text
                .replace("&cr;", "\n")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // 여러 키워드 후보를 순서대로 검사해서 날짜 범위를 찾는 메서드
    private DateRange findDateRangeByKeywords(String text, List<String> keywords) {

        // 키워드를 하나씩 검사
        for (String keyword : keywords) {

            // 현재 키워드 주변 텍스트 조각들을 가져옴
            List<String> windows = findWindows(text, keyword, 500);

            // 각 window 안에서 날짜 2개를 찾음
            for (String window : windows) {

                Pattern pattern = Pattern.compile(
                        "(" + DATE_REGEX + ").{0,120}?(?:~|-|부터|에서).{0,120}?(" + DATE_REGEX + ")"
                );

                Matcher matcher = pattern.matcher(window);

                if (matcher.find()) {
                    LocalDate start = ExternalDateParser.parseFlexibleDate(matcher.group(1));
                    LocalDate end = ExternalDateParser.parseFlexibleDate(matcher.group(2));

                    return new DateRange(start, end);
                }
            }
        }

        return new DateRange(null, null);
    }

    // 여러 키워드 후보를 순서대로 검사해서 날짜 하나를 찾는 메서드
    private LocalDate findDateByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 500);

            for (String window : windows) {
                Matcher matcher = Pattern.compile(DATE_REGEX).matcher(window);

                if (matcher.find()) {
                    return ExternalDateParser.parseFlexibleDate(matcher.group());
                }
            }
        }

        return null;
    }

    // 여러 키워드 후보를 순서대로 검사해서 가격 범위를 찾는 메서드입니다.
    private PriceRange findPriceRangeByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 500);

            for (String window : windows) {
                Pattern pattern = Pattern.compile(
                        "(" + NUMBER_REGEX + ")\\s*원?.{0,80}?(?:~|-|부터|에서).{0,80}?(" + NUMBER_REGEX + ")\\s*원?"
                );

                Matcher matcher = pattern.matcher(window);

                if (matcher.find()) {
                    BigDecimal min = ExternalNumberParser.toBigDecimal(matcher.group(1));
                    BigDecimal max = ExternalNumberParser.toBigDecimal(matcher.group(2));

                    return new PriceRange(min, max);
                }
            }
        }

        return new PriceRange(null, null);
    }

    // 여러 키워드 후보를 순서대로 검사해서 단일 금액을 찾는 메서드
    private BigDecimal findMoneyByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 500);

            for (String window : windows) {
                Pattern pattern = Pattern.compile("(" + NUMBER_REGEX + ")\\s*원");

                Matcher matcher = pattern.matcher(window);

                if (matcher.find()) {
                    return ExternalNumberParser.toBigDecimal(matcher.group(1));
                }
            }
        }

        return null;
    }

    // 공모주식수를 우선순위 기반으로 찾는 메서드
    private Long findShareCountByPriority(String text) {

        // 1순위: 공시 앞부분의 "모집 또는 매출 증권의 종류 : 기명식 보통주 65,450,000주" 같은 문구입니다.
        Long fromSecurityType = findShareCountByKeywords(
                text,
                List.of("모집 또는 매출 증권의 종류", "모집 또는 매출할 증권의 종류", "모집매출 증권의 종류")
        );

        if (fromSecurityType != null) {
            return fromSecurityType;
        }

        // 2순위: 명시적인 공모주식수 표현
        Long fromOfferingKeyword = findShareCountByKeywords(
                text,
                List.of("공모주식수", "공모 주식수", "모집주식수", "모집 주식수", "모집(매출) 주식수")
        );

        if (fromOfferingKeyword != null) {
            return fromOfferingKeyword;
        }

        // 3순위: 모집/매출 수량 표현
        return findShareCountByKeywords(
                text,
                List.of("모집수량", "매출수량", "모집 수량", "매출 수량")
        );
    }

    // 여러 키워드 후보를 순서대로 검사해서 주식 수를 찾는 메서드
    private Long findShareCountByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 600);

            Long bestCandidate = null;

            for (String window : windows) {
                Pattern pattern = Pattern.compile("(" + NUMBER_REGEX + ")\\s*주");

                Matcher matcher = pattern.matcher(window);

                while (matcher.find()) {
                    Long value = ExternalNumberParser.toLong(matcher.group(1));

                    if (value == null) {
                        continue;
                    }

                    // 같은 window 안에서 여러 후보가 나오면 더 큰 값을 우선합니다.
                    // 공모주식수는 우리사주/기관/일반 배정 물량보다 전체 수량이 더 큰 경우가 많기 때문입니다.
                    if (bestCandidate == null || value > bestCandidate) {
                        bestCandidate = value;
                    }
                }
            }

            if (bestCandidate != null) {
                return bestCandidate;
            }
        }

        return null;
    }

    // 여러 키워드 후보를 순서대로 검사해서 기관경쟁률을 찾는 메서드
    private BigDecimal findCompetitionRateByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 700);

            BigDecimal bestCandidate = null;

            for (String window : windows) {

                // 1732.83:1, 1732.83 대 1, 1,732.83:1 같은 형태를 찾습니다.
                Pattern pattern = Pattern.compile(
                        "(" + NUMBER_REGEX + "(?:\\.\\d+)?)\\s*(?:[:：]|대)\\s*1"
                );

                Matcher matcher = pattern.matcher(window);

                while (matcher.find()) {
                    BigDecimal value = ExternalNumberParser.toBigDecimal(matcher.group(1));

                    if (value == null) {
                        continue;
                    }

                    // 경쟁률 후보가 여러 개면 더 큰 값을 우선합니다.
                    // 의무보유확약 비율처럼 작은 수치가 잘못 잡히는 것을 줄이기 위한 선택입니다.
                    if (bestCandidate == null || value.compareTo(bestCandidate) > 0) {
                        bestCandidate = value;
                    }
                }
            }

            if (bestCandidate != null) {
                return bestCandidate;
            }
        }

        return null;
    }

    // 여러 키워드 후보를 순서대로 검사해서 퍼센트 값을 0~1 비율로 변환하는 메서드
    private BigDecimal findPercentRatioByKeywords(String text, List<String> keywords) {

        for (String keyword : keywords) {
            List<String> windows = findWindows(text, keyword, 700);

            BigDecimal bestPercent = null;

            for (String window : windows) {
                Pattern pattern = Pattern.compile("(" + NUMBER_REGEX + "(?:\\.\\d+)?)\\s*%");

                Matcher matcher = pattern.matcher(window);

                while (matcher.find()) {
                    BigDecimal percent = ExternalNumberParser.toBigDecimal(matcher.group(1));

                    if (percent == null) {
                        continue;
                    }

                    // 같은 영역에서 여러 퍼센트가 있으면 가장 큰 값을 우선합니다.
                    // 공시 표에서 신청수량/건수/비율이 같이 나올 때 대표 비율을 잡기 위한 단순 전략입니다.
                    if (bestPercent == null || percent.compareTo(bestPercent) > 0) {
                        bestPercent = percent;
                    }
                }
            }

            if (bestPercent != null) {
                return bestPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
        }

        return null;
    }

    // 특정 키워드가 등장하는 위치 주변의 문자열 조각들을 반환하는 메서드
    private List<String> findWindows(String text, String keyword, int windowSize) {

        List<String> windows = new ArrayList<>();

        if (text == null || text.isBlank() || keyword == null || keyword.isBlank()) {
            return windows;
        }

        int fromIndex = 0;

        while (fromIndex < text.length()) {
            int keywordIndex = text.indexOf(keyword, fromIndex);

            if (keywordIndex < 0) {
                break;
            }

            int start = Math.max(0, keywordIndex - 100);
            int end = Math.min(text.length(), keywordIndex + windowSize);

            windows.add(text.substring(start, end));

            fromIndex = keywordIndex + keyword.length();
        }

        return windows;
    }

    // 날짜 범위를 표현하는 내부 record
    private record DateRange(LocalDate start, LocalDate end) {
    }

    // 가격 범위를 표현하는 내부 record
    private record PriceRange(BigDecimal min, BigDecimal max) {
    }
}