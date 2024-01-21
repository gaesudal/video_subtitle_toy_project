package com.sudal.video.controller;

import com.sudal.video.exception.ApiException;
import com.sudal.video.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author SUDAL
 */
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        return ResponseEntity.badRequest().body(ApiResponse.of(e));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleException(ApiException e) {
        return ResponseEntity.badRequest().body(ApiResponse.of(e));
    }
}
