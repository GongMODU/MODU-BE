import json
import sys
from youtube_transcript_api import YouTubeTranscriptApi
from youtube_transcript_api._errors import (
    NoTranscriptFound,
    TranscriptsDisabled,
    VideoUnavailable
)

def main():
    if len(sys.argv) < 2:
        print(json.dumps({
            "success": False,
            "errorType": "INVALID_ARGUMENT",
            "message": "videoId argument is required"
        }, ensure_ascii=False))
        sys.exit(1)

    video_id = sys.argv[1]

    try:
        ytt_api = YouTubeTranscriptApi()
        transcript_list = ytt_api.list(video_id)  # ← 1.x는 list()

        transcript = None
        selected_language = None
        selected_type = None

        # 1순위: 한국어 수동 자막
        try:
            transcript = transcript_list.find_manually_created_transcript(["ko"])
            selected_language = "ko"
            selected_type = "manual"
        except NoTranscriptFound:
            pass

        # 2순위: 영어 수동 자막
        if transcript is None:
            try:
                transcript = transcript_list.find_manually_created_transcript(["en"])
                selected_language = "en"
                selected_type = "manual"
            except NoTranscriptFound:
                pass

        # 3순위: 한국어 자동 생성
        if transcript is None:
            try:
                transcript = transcript_list.find_generated_transcript(["ko"])
                selected_language = "ko"
                selected_type = "generated"
            except NoTranscriptFound:
                pass

        # 4순위: 영어 자동 생성
        if transcript is None:
            try:
                transcript = transcript_list.find_generated_transcript(["en"])
                selected_language = "en"
                selected_type = "generated"
            except NoTranscriptFound:
                pass

        if transcript is None:
            print(json.dumps({
                "success": False,
                "errorType": "NO_TRANSCRIPT",
                "message": "No Korean or English transcript found"
            }, ensure_ascii=False))
            sys.exit(2)

        fetched = transcript.fetch()

        segments = []
        full_text_parts = []

        # 1.x 버전: FetchedTranscriptSnippet 객체로 반환됨
        for item in fetched:
            text = item.text
            segments.append({
                "text": text,
                "start": item.start,
                "duration": item.duration
            })
            full_text_parts.append(text)

        print(json.dumps({
            "success": True,
            "videoId": video_id,
            "language": selected_language,
            "transcriptType": selected_type,
            "text": " ".join(full_text_parts),
            "segments": segments
        }, ensure_ascii=False))

    except TranscriptsDisabled:
        print(json.dumps({
            "success": False,
            "errorType": "TRANSCRIPTS_DISABLED",
            "message": "Transcripts are disabled for this video"
        }, ensure_ascii=False))
        sys.exit(3)

    except VideoUnavailable:
        print(json.dumps({
            "success": False,
            "errorType": "VIDEO_UNAVAILABLE",
            "message": "Video is unavailable"
        }, ensure_ascii=False))
        sys.exit(4)

    except Exception as e:
        print(json.dumps({
            "success": False,
            "errorType": "UNKNOWN",
            "message": str(e)
        }, ensure_ascii=False))
        sys.exit(5)

if __name__ == "__main__":
    main()