package com.sudal.video.model;

import static com.sudal.video.constant.ResultCode.YOUTUBE_URL_INVALID;

import com.sudal.video.exception.ApiException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author SUDAL
 */
@Getter
@Setter
@ToString
public class ApiRequest {
    String url;
    String model;
    String time;
    List<String> subtitles;

    public boolean isYoutubeUrl() {
        Pattern pattern = Pattern.compile(
                "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]*).*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.url);
        if (matcher.matches()) {
            return true;
        }
        throw new ApiException(YOUTUBE_URL_INVALID);
    }


}
