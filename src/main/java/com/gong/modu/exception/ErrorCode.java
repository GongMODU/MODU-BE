package com.gong.modu.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    EXPIRED_RESET_TOKEN(HttpStatus.BAD_REQUEST, "비밀번호 재설정 링크가 만료되었습니다."),
    USED_RESET_TOKEN(HttpStatus.BAD_REQUEST, "이미 사용된 비밀번호 재설정 링크입니다."),
    RESET_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "비밀번호 재설정 토큰을 찾을 수 없습니다."),
    SOCIAL_LOGIN_NOT_SUPPORTED_FOR_PASSWORD_RESET(HttpStatus.BAD_REQUEST, "소셜 로그인 계정은 비밀번호 재설정을 지원하지 않습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // YouTube
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "재생목록을 찾을 수 없습니다."),
    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "선택 가능한 YouTube 영상이 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}