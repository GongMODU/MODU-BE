package com.gong.modu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

// Spring Cache를 Redis 기반으로 사용하기 위한 설정 클래스
@EnableCaching
@Configuration
public class RedisCacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 기본 캐시용 JSON 직렬화기
        // youtubeLatestVideos, youtubeSummaries, youtubeUploadsPlaylists 등에 사용
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer();

        // 자막 캐시 전용 JDK 직렬화기
        // TranscriptResult를 LinkedHashMap이 아니라 실제 객체 형태로 복원하기 위해 사용
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

        // 대부분의 캐시에 적용할 기본 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                )
                .disableCachingNullValues();

        // youtubeTranscripts 캐시에만 적용할 설정
        // 자막 캐시: JDK 직렬화
        RedisCacheConfiguration transcriptCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(14))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer)
                )
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(
                "youtubeLatestVideos",
                defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        cacheConfigurations.put(
                "youtubeTranscripts",
                transcriptCacheConfig.entryTtl(Duration.ofDays(14))

        );

        cacheConfigurations.put(
                "youtubeSummaries",
                defaultConfig.entryTtl(Duration.ofDays(14))
        );

        // 추후 사용 예정
        cacheConfigurations.put(
                "youtubeUploadsPlaylists",
                defaultConfig.entryTtl(Duration.ofDays(30))

        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
