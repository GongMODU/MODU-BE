package com.gong.modu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

// YouTube 관련 설정값을 application.properties에서 읽어오는 클래스
@ConfigurationProperties(prefix = "youtube")
public class YouTubeProperties {

    private String apiKey;
    private List<String> channelIds;
    private int latestVideoCountPerChannel = 5;
    private int transcriptRetryCount = 10;
    private String cookiesPath;

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
}
