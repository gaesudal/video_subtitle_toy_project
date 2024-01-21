package com.sudal.video.controller;

import com.sudal.video.exception.ApiException;
import com.sudal.video.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author SUDAL
 */
@Slf4j
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(ApiResponse.of(e));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleException(ApiException e) {
        log.error("ApiException : {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(ApiResponse.of(e));
    }
}
