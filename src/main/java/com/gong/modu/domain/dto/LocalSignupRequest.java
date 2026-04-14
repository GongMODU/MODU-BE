package com.gong.modu.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalSignupRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,16}$",
            message = "비밀번호는 영문 대소문자 각 1개 이상, 특수문자(!@#$%^&*) 1개 이상, 8~16자여야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z]{2,8}$",
            message = "닉네임은 한글 또는 영문 2~8자여야 합니다."
    )
    private String nickname;
}
