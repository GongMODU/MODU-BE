package com.gong.modu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

// YouTube 관련 설정값을 application.properties에서 읽어오는 클래스
@ConfigurationProperties(prefix = "youtube")
public class YouTubeProperties {

    // YouTube Data API를 호출할 때 사용하는 API Key
    // properties의 youtube.api-key 값이 들어옴
    private String apiKey;

    // 사전에 수집 대상으로 지정한 YouTube 채널 ID 목록
    // properties의 youtube.channel-ids 값이 List<String>으로 바인딩됨
    private List<String> channelIds;

    // 채널 하나당 최신 영상 몇 개를 가져올지
    // properties의 youtube.latest-video-count-per-channel 값이 들어옴
    private int latestVideoCountPerChannel = 3;

    // 자막 추출에 실패했을 때, 다른 영상으로 최대 몇 번 재시도할지
    // properties의 youtube.transcript-retry-count 값이 들어옴
    private int transcriptRetryCount = 3;

    // youtube_transcript_api에서 사용할 쿠키 파일 경로
    // properties의 youtube.cookies-path 값이 들어옴
    private String cookiesPath;

    // 한 번 수동 수집 API를 호출했을 때 목표로 하는 요약 개수
    // 예: 1이면 성공한 요약 1개만 만들고 종료
    // properties의 youtube.summary-target-count 값이 들어옴
    private int summaryTargetCount = 1;

    // 목표 요약 개수를 채우기 위해 전체적으로 몇 번까지 시도할지
    // 예: 3이면 전체 수집 루프를 최대 3번까지만 반복
    // properties의 youtube.summary-max-attempts 값이 들어옴
    private int summaryMaxAttempts = 3;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public int getLatestVideoCountPerChannel() {
        return latestVideoCountPerChannel;
    }

    public void setLatestVideoCountPerChannel(int latestVideoCountPerChannel) {
        this.latestVideoCountPerChannel = latestVideoCountPerChannel;
    }

    public int getTranscriptRetryCount() {
        return transcriptRetryCount;
    }

    public void setTranscriptRetryCount(int transcriptRetryCount) {
        this.transcriptRetryCount = transcriptRetryCount;
    }

    public String getCookiesPath() {
        return cookiesPath;
    }

    public void setCookiesPath(String cookiesPath) {
        this.cookiesPath = cookiesPath;
    }

    public int getSummaryTargetCount() {
        return summaryTargetCount;
    }

    public void setSummaryTargetCount(int summaryTargetCount) {
        this.summaryTargetCount = summaryTargetCount;
    }

    public int getSummaryMaxAttempts() {
        return summaryMaxAttempts;
    }

    public void setSummaryMaxAttempts(int summaryMaxAttempts) {
        this.summaryMaxAttempts = summaryMaxAttempts;
    }
}
