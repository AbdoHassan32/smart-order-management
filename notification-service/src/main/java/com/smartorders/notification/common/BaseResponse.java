package com.smartorders.notification.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public BaseResponse() {}

    public static <T> BaseResponse<T> success(String message, T data) {
        BaseResponse<T> r = new BaseResponse<>();
        r.success = true;
        r.message = message;
        r.data = data;
        r.timestamp = LocalDateTime.now();
        return r;
    }

    public static <T> BaseResponse<T> success(String message) {
        BaseResponse<T> r = new BaseResponse<>();
        r.success = true;
        r.message = message;
        r.timestamp = LocalDateTime.now();
        return r;
    }

}