package com.pankaj.complaintmanagement.exception.custom;


public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message){
        super(message);
    }
}
