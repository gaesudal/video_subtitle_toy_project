package com.sudal.video.controller;


import com.sudal.video.constant.ResultCode;
import com.sudal.video.model.ApiRequest;
import com.sudal.video.model.ApiResponse;
import com.sudal.video.service.ApiService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SUDAL
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class ApiController {

    private final ApiService apiService;

    @PostMapping("/download/youtube")
    public ResponseEntity<ApiResponse> downloadYouTubeVideo(@RequestBody ApiRequest apiRequest) {

        String url = apiRequest.getUrl().trim();
        String model = apiRequest.getModel().trim();

        log.info("Download Youtube Video - URL : {}", url);
        log.info("Download Youtube Video - Whisper model : {}", model);

        apiRequest.isYoutubeUrl();

        String fileName = UUID.randomUUID().toString();

        apiService.downloadYouTubeVideo(url,fileName);
        apiService.convertWebmToMp3(fileName);
        apiService.makeThumbnail(fileName, "1");
        apiService.makeSubtitle(fileName, model);

        return new ResponseEntity<>(
                ApiResponse.of(
                        ResultCode.OK,
                        fileName
                ), HttpStatus.OK);
    }

    @GetMapping("/download/subtitle/{fileName}")
    public ResponseEntity<String> downloadSubtitle(@PathVariable String fileName) {
        log.info("Download Subtitle - fileName : {}", fileName);
        return new ResponseEntity<>(apiService.downloadSubtitle(fileName), HttpStatus.OK);
    }

    @PutMapping("/update/subtitle/{fileName}")
    public ResponseEntity<ApiResponse> updateSubtitle(@PathVariable String fileName, @RequestBody ApiRequest apiRequest) {

        List<String> subtitles = apiRequest.getSubtitles();

        log.info("Update Subtitle - fileName : {}", fileName);
        log.info("Update Subtitle - subtitles : {}", subtitles.toString());

        apiService.updateSubtitle(fileName, subtitles);

        String updateFileName = fileName.concat("_update");

        return new ResponseEntity<>(ApiResponse.of(
                ResultCode.OK,
                updateFileName
        ), HttpStatus.OK);
    }

    @PostMapping("/update/thumbnail/{fileName}")
    public ResponseEntity<ApiResponse> updateThumbnail(@PathVariable String fileName, @RequestBody ApiRequest apiRequest) {

        String time = apiRequest.getTime().trim();
        log.info("Update Thumbnail - fileName : {}", fileName);
        log.info("Update Thumbnail - time : {}", time);

        apiService.makeThumbnail(fileName, time);

        return new ResponseEntity<>(ApiResponse.of(
                        ResultCode.OK,
                        fileName
                ), HttpStatus.OK);
    }
}
