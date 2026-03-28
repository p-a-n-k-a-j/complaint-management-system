package com.pankaj.complaintmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotBlank(message = "password is mandatory! it can't be empty")
    private String password;

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
