package com.sudal.video.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sudal.video.constant.ResultCode;
import com.sudal.video.exception.ApiException;
import lombok.Getter;

/**
 * @author SUDAL
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String code;
    private String message;
    private String fileName;

    public ApiResponse(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(ResultCode resultCode, String fileName) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.fileName = fileName;
    }

    public static ApiResponse of(ResultCode resultCode) {
        return new ApiResponse(resultCode);
    }

    public static ApiResponse of(ResultCode resultCode, String fileName) {
        return new ApiResponse(resultCode, fileName);
    }

    public static ApiResponse of(ApiException e) {
        return new ApiResponse(e.getCode(), e.getMessage());
    }

    public static ApiResponse of(Exception e) {
        return new ApiResponse(ResultCode.FAIL);
    }
}
