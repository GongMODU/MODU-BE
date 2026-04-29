package com.gong.modu.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InvestmentAnswerRequest {

    @NotNull
    @Min(0) @Max(2)
    private Integer q1;

    @NotNull
    @Min(0) @Max(3)
    private Integer q2;

    @NotNull
    @Min(0) @Max(2)
    private Integer q3;

    @NotNull
    @Min(0) @Max(3)
    private Integer q4;

    @NotNull
    @Min(0) @Max(3)
    private Integer q5;

    @NotNull
    @Min(0) @Max(3)
    private Integer q6;

    @NotNull
    @Min(0) @Max(3)
    private Integer q7;

    @NotNull
    @Min(0) @Max(2)
    private Integer q8;

    @NotNull
    @Min(0) @Max(3)
    private Integer q9;

    @NotNull
    @Min(0) @Max(3)
    private Integer q10;
}
