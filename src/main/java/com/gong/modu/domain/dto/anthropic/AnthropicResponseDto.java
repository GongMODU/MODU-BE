package com.gong.modu.domain.dto.anthropic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class AnthropicResponseDto {

    private List<ContentBlock> content;
    private Usage usage;

    public String getFirstText() {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.get(0).getText();
    }

    @Getter
    public static class ContentBlock {
        private String type;
        private String text;
    }

    @Getter
    public static class Usage {
        @JsonProperty("input_tokens")
        private int inputTokens;

        @JsonProperty("output_tokens")
        private int outputTokens;
    }
}
