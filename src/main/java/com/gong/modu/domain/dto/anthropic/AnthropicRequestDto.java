package com.gong.modu.domain.dto.anthropic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnthropicRequestDto {

    private String model;

    @JsonProperty("max_tokens")
    private int maxTokens;

    private List<AnthropicMessageDto> messages;
}
