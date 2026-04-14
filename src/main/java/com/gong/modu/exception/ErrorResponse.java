package com.gong.modu.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 에러 응답 공통 포맷
 * - status: HTTP 상태코드
 * - code: ErrorCode enum 이름 (예: USER_NOT_FOUND)
 * - message: 클라이언트에 노출할 한국어 메시지
 * - timestamp: 에러 발생 시각
 */
@Getter
@Builder
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    // ErrorCode로부터 ErrorResponse 생성
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}