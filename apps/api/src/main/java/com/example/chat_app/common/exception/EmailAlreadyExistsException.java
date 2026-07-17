package com.example.chat_app.common.exception;

public class EmailAlreadyExistsException extends RuntimeException
{
    public EmailAlreadyExistsException(){
        super("Email already exists");
    }
}
