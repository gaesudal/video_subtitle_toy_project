package com.sudal.video.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SUDAL
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String resultCode;
    private String message;
    private String fileName;

}
