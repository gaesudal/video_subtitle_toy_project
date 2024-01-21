package com.sudal.video.constant;

import lombok.Getter;

/**
 * @author SUDAL
 */
@Getter
public enum ResultCode {

    OK("0", "OK"),
    FAIL("1", "FAIL"),

    YOUTUBE_DOWNLOAD_ERROR("1000", "Youtube Download Error"),
    WEBM_TO_MP3_CONVERT_ERROR("1001", "Webm to Mp3 Convert Error"),
    THUMBNAIL_MAKE_ERROR("1002", "Thumbnail Make Error"),
    SUBTITLE_MAKE_ERROR("1003", "Subtitle Make Error"),
    FILE_NOT_FOUND("1004", "File Not Found"),
    FILE_DELETE_ERROR("1005", "File Delete Error"),
    YOUTUBE_URL_INVALID("1006", "Youtube Url Invalid"),
    SUBTITLE_UPDATE_ERROR("1007", "Subtitle Update Error"),
    ;

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
