package com.sudal.video.exception;

import com.sudal.video.constant.ResultCode;
import lombok.Getter;

/**
 * @author SUDAL
 */
@Getter
public class ApiException extends RuntimeException{

    public final String code;
    public final String message;

    public ApiException(ResultCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ApiException () {
        super(ResultCode.FAIL.getMessage());
        this.code = ResultCode.FAIL.getCode();
        this.message = ResultCode.FAIL.getMessage();
    }

    public ApiException (Throwable throwable) {
        super(throwable);
        this.code = ResultCode.FAIL.getCode();
        this.message = ResultCode.FAIL.getMessage();
    }
}
