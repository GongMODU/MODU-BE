package com.gong.modu.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetConfirmRequest {

    @NotBlank(message = "토큰을 입력해주세요.")
    private String token;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,16}$",
            message = "비밀번호는 영문 대소문자 각 1개 이상, 특수문자(!@#$%^&*) 1개 이상, 8~16자여야 합니다."
    )
    private String newPassword;
}
