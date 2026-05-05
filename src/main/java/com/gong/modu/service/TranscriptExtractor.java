package com.gong.modu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gong.modu.config.YouTubeProperties;
import com.gong.modu.domain.dto.TranscriptResult;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// Python youtube-transcript-api 스크립트를 실행해서 자막을 가져오는 클래스
@Component
public class TranscriptExtractor {

    private static final Duration TIMEOUT = Duration.ofMinutes(5);

    private final ObjectMapper objectMapper;
    private final YouTubeProperties properties;

    public TranscriptExtractor(ObjectMapper objectMapper, YouTubeProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public TranscriptResult extract(String videoId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            List<String> command = new ArrayList<>(Arrays.asList(
                    ".venv/bin/python3",
                    "src/main/resources/scripts/get_transcript.py",
                    videoId
            ));
            if (properties.getCookiesPath() != null && !properties.getCookiesPath().isBlank()) {
                command.add(properties.getCookiesPath());
            }
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 에러 스트림도 표준 출력에 합쳐서 에러 메시지를 Java에서 함께 읽을 수 있도록 함
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 기존: process.waitFor()로 Python 종료를 먼저 기다린 뒤 stdout을 읽음
            // 변경: 롱폼 자막처럼 출력 JSON이 길 경우엔 OS pipe 버퍼가 꽉 차므로 Python은 출력하다가 멈추고, Java는 Python 종료만 기다리다가 타임아웃이 발생할 수 있음.
            // 따라서 프로세스 실행 직후 stdout 읽기를 별도 스레드에서 바로 시작해야 함.
            Future<String> outputFuture = executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
                )) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            });

            boolean finished = process.waitFor(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();

                return new TranscriptResult(
                        false,
                        videoId,
                        null,
                        null,
                        null,
                        null,
                        "TIMEOUT",
                        "Transcript extraction timed out"
                );
            }

            String output = outputFuture.get(5, TimeUnit.SECONDS);

            if (output == null || output.isBlank()) {
                return new TranscriptResult(
                        false,
                        videoId,
                        null,
                        null,
                        null,
                        null,
                        "EMPTY_OUTPUT",
                        "Transcript script returned empty output"
                );
            }

            return objectMapper.readValue(output, TranscriptResult.class);

        } catch (TimeoutException e) {
            return new TranscriptResult(
                    false,
                    videoId,
                    null,
                    null,
                    null,
                    null,
                    "OUTPUT_READ_TIMEOUT",
                    "Timed out while reading transcript script output"
            );
        } catch (Exception e) {
            return new TranscriptResult(
                    false,
                    videoId,
                    null,
                    null,
                    null,
                    null,
                    "PROCESS_ERROR",
                    e.getMessage()
            );
        } finally {
            executor.shutdownNow();
        }
    }
}
