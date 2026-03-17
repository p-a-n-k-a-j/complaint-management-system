package com.pankaj.complaintmanagement.exception.payload;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String message;
    private final String error;
    private final int status;
    private final String path;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public ErrorResponse(Builder builder) {
        this.path = fetchCurrentPath();
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.timestamp = LocalDateTime.now();
    }

    public String fetchCurrentPath(){
       ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
           return attributes.getRequest().getRequestURI();
        }
        return "Unknown";
    }

    static class Builder{
        private String message;
        private int status;
        private String error;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(HttpStatus httpStatus) {
            this.status = httpStatus.value();
            this.error = httpStatus.getReasonPhrase();
            return this;
        }
        public ErrorResponse build(){
            return new ErrorResponse(this);
        }

    }
}
