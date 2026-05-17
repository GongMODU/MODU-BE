package com.gong.modu.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 외부 API에서 내려오는 날짜 문자열을 LocalDate로 변환하는 유틸 클래스
public final class ExternalDateParser {

    // yyyyMMdd 형식 변환용 formatter
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 유틸 클래스는 객체를 만들 필요 없으므로 private 생성자로 인스턴스 생성을 막음
    private ExternalDateParser() {}

    // yyyyMMdd 형식 문자열을 LocalDate로 변환하는 메서드
    public static LocalDate parseBasicDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value.trim(), BASIC_DATE);
    }

    // 다양한 날짜 문자열에서 숫자만 추출해 yyyyMMdd로 변환하는 메서드
    public static LocalDate parseFlexibleDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.length() < 8) { // 날짜는 최소 8자리
            return null;
        }

        return LocalDate.parse(digits.substring(0, 8), BASIC_DATE);
    }

    // 날짜 범위 문자열에서 첫 번째 날짜를 추출하는 메서드
    public static LocalDate parseFirstDateFromRange(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // 날짜처럼 보이는 패턴을 찾기 위한 정규식
        Matcher matcher = Pattern
                .compile("\\d{4}[^0-9]?\\d{1,2}[^0-9]?\\d{1,2}").matcher(value);

        // 첫 번째 날짜를 찾으면 시작일로 보고 반환
        if (matcher.find()) {
            return parseFlexibleDate(matcher.group());
        }

        return null;
    }

    // 날짜 범위 문자열에서 마지막 날짜를 추출하는 메서드
    public static LocalDate parseLastDateFromRange(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        Matcher matcher = Pattern
                .compile("\\d{4}[^0-9]?\\d{1,2}[^0-9]?\\d{1,2}").matcher(value);

        LocalDate lastDate = null;

        while (matcher.find()) { // 정규식에 맞는 날짜를 찾을 때마다 반복
            // 반복문이 끝나면 lastDate에는 가장 마지막 날짜가 남게 됨
            lastDate = parseFlexibleDate(matcher.group());
        }

        return lastDate;
    }
}
