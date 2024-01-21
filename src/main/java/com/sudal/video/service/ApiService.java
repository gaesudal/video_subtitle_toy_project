package com.sudal.video.service;

import static com.sudal.video.constant.ResultCode.FILE_NOT_FOUND;
import static com.sudal.video.constant.ResultCode.SUBTITLE_MAKE_ERROR;
import static com.sudal.video.constant.ResultCode.SUBTITLE_UPDATE_ERROR;
import static com.sudal.video.constant.ResultCode.THUMBNAIL_MAKE_ERROR;
import static com.sudal.video.constant.ResultCode.WEBM_TO_MP3_CONVERT_ERROR;
import static com.sudal.video.constant.ResultCode.YOUTUBE_DOWNLOAD_ERROR;
import static com.sudal.video.constant.ResultCode.YOUTUBE_URL_INVALID;

import com.sudal.video.exception.ApiException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author SUDAL
 */
@Slf4j
@Service
public class ApiService {


    public static String downloadDir;

    @Value("${file.download-dir}")
    public void setDownloadDir(String downloadDir) {
        ApiService.downloadDir = downloadDir;
    }

    public boolean downloadYouTubeVideo(String url, String fileName) {
        try {

            log.info("url : {}", url);
            log.info("fileName : {}", fileName);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "yt-dlp", "-P", downloadDir.concat("video").concat(File.separator),
                    "--ignore-errors",
                    "--merge-output-format", "webm",
                    "-o", fileName,
                    url);

            return executeCommand(processBuilder) == 0;
        } catch (IOException | InterruptedException e) {
            log.error("dowloadYoutubeVideo : {}", e);
            throw new ApiException(YOUTUBE_DOWNLOAD_ERROR);
        }
    }

    public boolean convertWebmToMp3(String fileName) {
        try {
            String webmFileName = fileName.concat(".webm");

            log.info("webmFileName : {}", webmFileName);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i",
                    downloadDir.concat("video\\").concat(webmFileName),
                    "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3",
                    downloadDir.concat("audio\\").concat(fileName)
                            .concat(".mp3"));
            return executeCommand(processBuilder) == 0;
        } catch (IOException | InterruptedException e) {
            log.error("convertWebmToMp3 : {}", e);
            throw new ApiException(WEBM_TO_MP3_CONVERT_ERROR);
        }
    }

    public boolean makeThumbnail(String fileName, String second) {
        try {

            String webmFileName = fileName.concat(".webm");
            log.info("webmFileName : {}", webmFileName);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i",
                    downloadDir.concat("video").concat(File.separator).concat(webmFileName),
                    "-ss", second, "-vframes", "1", "-an", "-s", "640x360",
                    downloadDir.concat("thumbnail").concat(File.separator).concat(fileName)
                            .concat(".jpg"));

            return executeCommand(processBuilder) == 0;
        } catch (IOException | InterruptedException e) {
            log.error("makeThumbnail : {}", e);
            throw new ApiException(THUMBNAIL_MAKE_ERROR);
        }
    }

    public boolean makeSubtitle(String fileName, String model) {
        try {

            ProcessBuilder processBuilder = new ProcessBuilder("python",
                    downloadDir.concat("test.py"),
                    model,
                    downloadDir.concat("audio").concat(File.separator).concat(fileName)
                            .concat(".mp3"),
                    downloadDir.concat("subtitle").concat(File.separator));

            processBuilder.environment().put("PYTHONIOENCODING", "UTF-8");
            processBuilder.redirectErrorStream(true);

            List<String> command = processBuilder.command();
            log.info("command : {}", makePrettyCommandLine(command));

            Process process = processBuilder.start();

            try (BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(),
                            "UTF-8"))) {

                String line;
                while ((line = inputReader.readLine()) != null) {
                    log.info("{}", line);
                }

                process.waitFor();

                File vttFile = new File(
                        downloadDir.concat("subtitle").concat(File.separator).concat(fileName)
                                .concat(".vtt"));

                if (vttFile.exists()) {
                    log.info("vttFile exists");
                } else {
                    throw new ApiException(FILE_NOT_FOUND);
                }

                int exitCode = process.exitValue();
                return exitCode == 0;

            } finally {
                if (process.isAlive()) {
                    process.destroy();
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error("makeSubtitle : {}", e);
            throw new ApiException(SUBTITLE_MAKE_ERROR);
        }
    }

    public String makePrettyCommandLine(List<String> command) {

        StringBuilder sb = new StringBuilder();

        for (String s : command) {
            sb.append(s).append(" ");
        }

        return sb.toString();
    }

    public String downloadSubtitle(String fileName) {
        Path filePath = Path.of(
                downloadDir.concat("subtitle").concat(File.separator).concat(fileName)
                        .concat(".vtt"));
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new ApiException(FILE_NOT_FOUND);
        }
    }

    public boolean updateSubtitle(String fileName, List<String> subtitles) {
        String updateFileName = fileName.concat("_update");
        String filePath = downloadDir.concat("subtitle").concat(File.separator)
                .concat(updateFileName).concat(".vtt");

        try (FileWriter fileWriter = new FileWriter(filePath, false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("WEBVTT\n\n");
            for (String subtitle : subtitles) {
                bufferedWriter.write(subtitle);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new ApiException(SUBTITLE_UPDATE_ERROR);
        }
        return true;
    }

    private int executeCommand(ProcessBuilder pb) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = pb;

        processBuilder.redirectErrorStream(true);

        List<String> commands = processBuilder.command();
        log.info("commands : {}", makePrettyCommandLine(commands));

        Process process = processBuilder.start();

        try (BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = inputReader.readLine()) != null) {
                log.info("{}", line);
            }

            process.waitFor();
            int exitCode = process.exitValue();
            return process.exitValue();
        }
    }

}


