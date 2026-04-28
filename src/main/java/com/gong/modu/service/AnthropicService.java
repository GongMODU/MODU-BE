package com.gong.modu.service;

import com.gong.modu.domain.dto.anthropic.AnthropicMessageDto;
import com.gong.modu.domain.dto.anthropic.AnthropicRequestDto;
import com.gong.modu.domain.dto.anthropic.AnthropicResponseDto;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnthropicService {

    private final WebClient anthropicWebClient;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens:1024}")
    private int maxTokens;

    private static final String MESSAGES_ENDPOINT = "/v1/messages";

    public String call(List<AnthropicMessageDto> messages) {
        AnthropicRequestDto request = AnthropicRequestDto.builder()
                .model(model)
                .maxTokens(maxTokens)
                .messages(messages)
                .build();

        try {
            AnthropicResponseDto response = anthropicWebClient.post()
                    .uri(MESSAGES_ENDPOINT)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AnthropicResponseDto.class)
                    .block();

            if (response == null) {
                throw new CustomException(ErrorCode.CLAUDE_API_ERROR);
            }

            return response.getFirstText();

        } catch (WebClientResponseException e) {
            log.error("Claude API 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.CLAUDE_API_ERROR);
        }
    }
}
