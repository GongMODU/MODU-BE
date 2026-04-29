package com.gong.modu.domain.dto.anthropic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnthropicMessageDto {

    private String role;
    private String content;
}
