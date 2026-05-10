package com.pankaj.complaintmanagement.exception.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
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

    private ErrorResponse(Builder builder) {
        this.path = builder.path;
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.timestamp = LocalDateTime.now();
    }
/**
 * I never use this method because of data inconsistency or tight coupling with the web layer**/
    public String fetchCurrentPath(){
       ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
           return attributes.getRequest().getRequestURI();
        }
        return "Unknown";
    }

    public static class Builder{
        private String message;
        private int status;
        private String error;
        private String path;

        public Builder path(String path){
            this.path = path;
            return this;
        }

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
