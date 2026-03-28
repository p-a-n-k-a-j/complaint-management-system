package com.pankaj.complaintmanagement.auth.dto;

import com.pankaj.complaintmanagement.util.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class RegisterRequest {
    @NotBlank(message = "name can't be empty")
    private String name;
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotBlank(message = "password is mandatory! it can't be empty")
    private String password;



    public String getName() {
        return name;
    }

    public void setName( String name) {
        this.name = name;
    }

    public String getEmail() {
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
