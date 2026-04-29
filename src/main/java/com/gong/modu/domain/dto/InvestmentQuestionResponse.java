package com.gong.modu.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InvestmentQuestionResponse {

    private List<QuestionDto> questions;

    @Getter
    @Builder
    public static class QuestionDto {
        private int questionNumber;
        private String axis;
        private String content;
        private List<OptionDto> options;
    }

    @Getter
    @Builder
    public static class OptionDto {
        private int index;
        private String content;
    }
}
