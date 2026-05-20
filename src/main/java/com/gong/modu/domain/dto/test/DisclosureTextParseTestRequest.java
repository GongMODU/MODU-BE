package com.gong.modu.domain.dto.test;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisclosureTextParseTestRequest {

    // 테스트할 공시 원문 텍스트
    @NotBlank
    private String text;
}
