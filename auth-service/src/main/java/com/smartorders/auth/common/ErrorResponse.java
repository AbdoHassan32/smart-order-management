package com.smartorders.auth.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

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

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Map<String, String> getErrors() { return errors; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPath() { return path; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setPath(String path) { this.path = path; }
}