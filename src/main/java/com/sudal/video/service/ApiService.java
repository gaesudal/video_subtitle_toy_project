package com.sudal.video.service;

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

    public boolean downloadYouTubeVideo(String url, String fileName)
            throws IOException {

        log.info("url : {}", url);
        log.info("fileName : {}", fileName);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "yt-dlp", "-P", downloadDir.concat("video").concat(File.separator),
                "--ignore-errors",
                "--merge-output-format", "webm",
                "-o", fileName,
                url);

        processBuilder.redirectErrorStream(true);

        List<String> command = processBuilder.command();
        log.info("command : {}", makePrettyCommandLine(command));

        Process process = processBuilder.start();
            try (BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())))
            {

                String line;
                while ((line = inputReader.readLine()) != null) {
                    log.info("{}", line);
                }
                process.waitFor();
                int exitCode = process.exitValue();
                return exitCode == 0;
            } catch (IOException e) {
                log.error("{}", e);
                return false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (process.isAlive()) {
                    process.destroy();
                }
            }
    }

    public boolean convertWebmToMp3(String fileName) throws IOException, InterruptedException {
        try {
            String webmFileName = fileName.concat(".webm");

            log.info("webmFileName : {}", webmFileName);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i",
                    downloadDir.concat("video\\").concat(webmFileName),
                    "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3",
                    downloadDir.concat("audio\\").concat(fileName)
                            .concat(".mp3"));

            processBuilder.redirectErrorStream(true);

            List<String> command = processBuilder.command();
            log.info("command : {}", makePrettyCommandLine(command));

            Process process = processBuilder.start();

            try (BufferedReader inputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = inputReader.readLine()) != null) {
                    log.info("{}", line);
                }

                process.waitFor();
                int exitCode = process.exitValue();
                return exitCode == 0;
            } catch (IOException e) {
                log.error("{}", e);
                return false;
            } finally {
                if (process.isAlive()) {
                    process.destroy();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean makeThumbnail(String fileName, String second) throws IOException, InterruptedException {
        String webmFileName = fileName.concat(".webm");

        log.info("webmFileName : {}", webmFileName);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i",
                downloadDir.concat("video").concat(File.separator).concat(webmFileName),
                "-ss", second, "-vframes", "1", "-an", "-s", "640x360",
                downloadDir.concat("thumbnail").concat(File.separator).concat(fileName)
                        .concat(".jpg"));

        processBuilder.redirectErrorStream(true);

        List<String> command = processBuilder.command();
        log.info("command : {}", makePrettyCommandLine(command));

        Process process = processBuilder.start();

        try (BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = inputReader.readLine()) != null) {
                log.info("{}", line);
            }

            process.waitFor();
            int exitCode = process.exitValue();
            return exitCode == 0;
        }
        catch (IOException e) {
            log.error("{}", e);
            return false;
        } finally {
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    public boolean makeSubtitle(String fileName, String model)
            throws IOException, InterruptedException {

        ProcessBuilder processBuilder;

        processBuilder = new ProcessBuilder("python",
                downloadDir.concat("test.py"),
                model,
                downloadDir.concat("audio").concat(File.separator).concat(fileName).concat(".mp3"),
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

            File vttFile = new File(downloadDir.concat("subtitle").concat(File.separator).concat(fileName).concat(".vtt"));

            if (vttFile.exists()) {
                log.info("vttFile exists");
            } else {
                log.info("vttFile not exists");
            }

            int exitCode = process.exitValue();
            return exitCode == 0;

        } catch (IOException e) {
            log.error("{}", e);
            return false;
        } finally {
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    public String extractVideoId(String url) {
        Pattern pattern = Pattern.compile(
                "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]*).*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public String makePrettyCommandLine (List<String> command) {

        StringBuilder sb = new StringBuilder();

        for (String s : command) {
            sb.append(s).append(" ");
        }

        return sb.toString();
    }

    public String downloadSubtitle(String fileName) {
        Path filePath = Path.of(downloadDir.concat("subtitle").concat(File.separator).concat(fileName).concat(".vtt"));
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateSubtitle(String fileName, List<String> subtitles) {
        String updateFileName = fileName.concat("_update");
        String filePath = downloadDir.concat("subtitle").concat(File.separator).concat(updateFileName).concat(".vtt");

        try (FileWriter fileWriter = new FileWriter(filePath, false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            bufferedWriter.write("WEBVTT\n\n");
            for (String subtitle : subtitles) {
                bufferedWriter.write(subtitle);
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}


