package com.gong.modu.util;

import java.math.BigDecimal;

// 외부 API에서 문자열로 내려오는 숫자값을 Java 숫자 타입으로 변환하는 클래스
public final class ExternalNumberParser {

    private ExternalNumberParser() {}

    // 문자열을 Long 타입으로 변환하는 메서드
    public static Long toLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.replace(",", "").trim();
        if (normalized.isBlank() || "-".equals(normalized)) {
            return null;
        }

        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 문자열을 BigDecimal 타입으로 변환하는 메서드
    public static BigDecimal toBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.replace(",", "").trim();
        if (normalized.isBlank() || "-".equals(normalized)) {
            return null;
        }

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
