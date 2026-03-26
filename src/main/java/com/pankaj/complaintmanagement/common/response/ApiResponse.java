package com.pankaj.complaintmanagement.common.response;

public class ApiResponse <T>{
    private String message;
    private boolean success;
    private T data;
private ApiResponse(boolean success, String message, T data){
    this.success = success;
    this.message = message;
    this.data = data;
}

    public static <T> ApiResponse<T> success(String message, T data){
        return new ApiResponse<>(true, message, data);
    }
    public static <T> ApiResponse<T> success(String message){
    return new ApiResponse<>(true, message, null);
    }
}
