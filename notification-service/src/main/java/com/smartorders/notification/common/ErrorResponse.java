package com.smartorders.notification.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse() {}

    public static ErrorResponse of(String message, String path) {
        ErrorResponse r = new ErrorResponse();
        r.success = false;
        r.message = message;
        r.timestamp = LocalDateTime.now();
        r.path = path;
        return r;
    }

    public static ErrorResponse of(String message, Map<String, String> errors, String path) {
        ErrorResponse r = new ErrorResponse();
        r.success = false;
        r.message = message;
        r.errors = errors;
        r.timestamp = LocalDateTime.now();
        r.path = path;
        return r;
    }

}