from datetime import timedelta
import os
import whisper
import sys

def transcribe_audio(model, file_path, output_dir):
    model = whisper.load_model(model) # Change this to your desired model
    print("Whisper model loaded.")
    transcribe = model.transcribe(audio=file_path)
    segments = transcribe['segments']

    # 파일명만 추출 (확장자 포함)
    file_name_with_extension = os.path.basename(file_path)

    # 확장자를 제외한 파일명만 추출
    file_name, _ = os.path.splitext(file_name_with_extension)

    vttFilename = os.path.join(output_dir, f"{file_name}.vtt")
    with open(vttFilename, 'a', encoding='utf-8') as vttFile:
        vttFile.write("WEBVTT\n\n")

    for segment in segments:
        startTime = str(0)+str(timedelta(seconds=int(segment['start'])))+'.000'
        endTime = str(0)+str(timedelta(seconds=int(segment['end'])))+'.000'
        text = segment['text']
        segmentId = segment['id']+1
        segment = f"{segmentId}\n{startTime} --> {endTime}\n{text[1:] if text[0] is ' ' else text}\n\n"

        vttFilename = os.path.join(output_dir, f"{file_name}.vtt")
        with open(vttFilename, 'a', encoding='utf-8') as vttFile:
            vttFile.write(segment)

    return vttFilename

transcribe_audio(sys.argv[1], sys.argv[2], sys.argv[3])