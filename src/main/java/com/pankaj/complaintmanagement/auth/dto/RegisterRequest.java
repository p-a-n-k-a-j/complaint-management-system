package com.pankaj.complaintmanagement.auth.dto;

import com.pankaj.complaintmanagement.common.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class RegisterRequest {
    @NotBlank(message = "name can't be empty")
    private String name;
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotBlank(message = "password is mandatory! it can't be empty")
    private String password;
    @NotNull(message = "Gender is required")
    private Gender gender;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

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
