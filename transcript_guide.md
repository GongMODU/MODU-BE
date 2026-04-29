RandomTranscriptService 사용 방법

사용할 메서드:
- randomTranscriptService.getRandomTranscript()

반환 타입:
- RandomTranscriptResponse

주요 필드:
- videoId: YouTube 영상 ID
- videoUrl: 원본 영상 URL
- title: 영상 제목
- channelId: YouTube 채널 고유 ID
- channelTitle: 사람이 읽을 수 있는 채널명
- language: 추출된 자막 언어
- transcriptType: manual 또는 generated
- transcriptText: LLM 요약에 넣을 전체 자막 텍스트

LLM 요약에 직접 사용할 값:
- transcript.transcriptText()

요약 결과 화면에 함께 보여주면 좋은 값:
- transcript.title()
- transcript.channelTitle()
- transcript.videoUrl()