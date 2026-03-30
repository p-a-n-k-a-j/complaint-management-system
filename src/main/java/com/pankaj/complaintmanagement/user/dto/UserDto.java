package com.pankaj.complaintmanagement.user.dto;

import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.util.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDto {

    @NotNull(message = "id can't be null")
    private Long id;
    @NotBlank(message = "name can't be empty")
    private String name;
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotEmpty(message = "role can't be empty")
    private Set<UserRole> roles;
    @NotNull(message = "Account Status can't be null")
    private AccountStatus status;

    private LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    @NotBlank(message = "phone number is required")
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String imageUrl;
    private String bio;

    private UserDto(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.roles = builder.roles;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.phone = builder.phone;
        this.address = builder.address;
        this.city = builder.city;
        this.state = builder.state;
        this.pincode = builder.pincode;
        this.imageUrl = builder.imageUrl;
        this.bio = builder.bio;
        this.updatedAt = builder.updatedAt;
    }

    public  Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public  Set<UserRole> getRoles() {
        return roles;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public  String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public static class Builder{
        private Long id;
        private String name;
        private String email;
        private Set<UserRole> roles;
        private AccountStatus status;
        private LocalDateTime createdAt;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String imageUrl;
        private String bio;
        private LocalDateTime updatedAt;
        public Builder updatedAt(LocalDateTime updatedAt){
            this.updatedAt = updatedAt;
            return this;
        }
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder roles(Set<UserRole> roles) {
            this.roles = roles;
            return this;
        }

        public Builder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder pincode(String pincode) {
            this.pincode = pincode;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }
        public UserDto build(){
            return new UserDto(this);
        }
    }
}
