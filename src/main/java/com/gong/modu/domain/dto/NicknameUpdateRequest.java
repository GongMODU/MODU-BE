package com.gong.modu.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class NicknameUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z]{2,8}$",
            message = "닉네임은 한글 또는 영문 2~8자여야 합니다."
    )
    private String nickname;
}
