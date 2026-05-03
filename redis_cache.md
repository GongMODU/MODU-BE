YouTube 자막 추출 기능 Redis 캐시 적용 내용 공유드립니다.

현재 RandomTranscriptService.getRandomTranscript()를 호출하면,
등록된 YouTube 채널의 최신 영상 중 자막이 있는 랜덤 영상을 찾아서 RandomTranscriptResponse로 반환합니다.

반환 DTO 주요 필드:
- videoId: YouTube 영상 ID
- videoUrl: 원본 YouTube 링크
- title: 영상 제목
- channelId: YouTube 채널 ID
- channelTitle: 채널명
- language: 추출된 자막 언어
- transcriptType: manual 또는 generated
- transcriptText: LLM 요약에 사용할 전체 자막 텍스트

Redis 캐시 적용:
1. 최신 영상 pool
    - cacheName: youtubeLatestVideos
    - key: pool
    - TTL: 30분
    - 목적: YouTube Data API 호출 반복 방지

2. 자막 추출 결과
    - cacheName: youtubeTranscripts
    - key: videoId
    - TTL: 14일
    - 목적: 같은 영상 자막을 Python 스크립트로 반복 추출하지 않기

주의:
- RandomTranscriptService.getRandomTranscript() 자체는 캐싱하지 않았습니다.
- 이 메서드 자체를 캐싱하면 랜덤 결과가 고정될 수 있기 때문입니다.
- 대신 영상 후보 pool과 videoId별 자막 결과만 캐싱합니다.

LLM 요약 쪽 추천 캐시:
- 요약 결과도 videoId 기준으로 Redis 캐싱하는 것을 추천합니다.
- cacheName: youtubeSummaries
- key 추천: 'v1:' + videoId
- TTL: 14일

프롬프트가 바뀌면 key 버전을 v2로 바꾸면 됩니다.
예:
v1:abc123 → 기존 프롬프트 요약
v2:abc123 → 새 프롬프트 요약

요약 서비스 예시 흐름:
1. RandomTranscriptResponse transcript = randomTranscriptService.getRandomTranscript();
2. String summary = cachedSummaryService.summarize(transcript.videoId(), transcript.transcriptText());
3. 응답에는 title, channelTitle, videoUrl, summaryText를 함께 내려주면 됩니다.