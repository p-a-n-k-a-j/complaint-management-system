package com.pankaj.complaintmanagement.exception.custom;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message){super(message);}
}
