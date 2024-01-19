package com.sudal.video.controller;


import com.sudal.video.model.ApiResponse;
import com.sudal.video.model.Param;
import com.sudal.video.service.ApiService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/youtube-download")
    public ApiResponse downloadYouTubeVideo(@RequestBody HashMap<String, Object> map) throws IOException, InterruptedException {

        String url = map.get("url").toString().trim();
        log.info("Download Youtube Video - URL : {}", url);

        String fileName = UUID.randomUUID().toString();

        boolean isDownloaded = apiService.downloadYouTubeVideo(url,fileName);

        boolean isConverted = false;
        if (isDownloaded) {
            isConverted = apiService.convertWebmToMp3(fileName);
        }

        boolean isThumbnailMade = false;
        if (isConverted) {
            isThumbnailMade = apiService.makeThumbnail(fileName);
        }

        boolean isSubtitleMade = false;
        if (isThumbnailMade) {
            isSubtitleMade = apiService.makeSubtitle(fileName, "tiny");
        }

        ApiResponse apiResponse = new ApiResponse();
        if (isSubtitleMade) {
            apiResponse.setResultCode("200");
            apiResponse.setMessage("success");
            apiResponse.setFileName(fileName);
        } else {
            apiResponse.setResultCode("500");
            apiResponse.setMessage("fail");
        }
        return apiResponse;
    }

    @GetMapping("/download/subtitle/{fileName}")
    public String downloadSubtitle(@PathVariable String fileName) {
        log.info("Download Subtitle - fileName : {}", fileName);
        return apiService.downloadSubtitle(fileName);
    }

    @PutMapping("/update/subtitle/{fileName}")
    public ApiResponse updateSubtitle(@PathVariable String fileName, @RequestBody HashMap<String, Object> map) {

        List<String> subtitles = (List<String>) map.get("subtitles");

        log.info("Update Subtitle - fileName : {}", fileName);
        log.info("Update Subtitle - subtitles : {}", subtitles.toString());

        boolean isSubtitleUpdate = apiService.updateSubtitle(fileName, subtitles);

        ApiResponse apiResponse = new ApiResponse();
        if (isSubtitleUpdate) {
            apiResponse.setResultCode("200");
            apiResponse.setMessage("success");
            apiResponse.setFileName(fileName.concat("_update"));
        } else {
            apiResponse.setResultCode("500");
            apiResponse.setMessage("fail");
        }
        return apiResponse;
    }
}
