package com.gong.modu.domain.dto.anthropic;

import lombok.Getter;

import java.util.List;

@Getter
public class AnthropicResponseDto {

    private List<ContentBlock> content;

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
}
