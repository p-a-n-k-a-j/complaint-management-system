package com.pankaj.complaintmanagement.exception.custom;


public class UserProfileNotFoundException extends RuntimeException{
    public UserProfileNotFoundException(String message){
        super(message);
    }
}
