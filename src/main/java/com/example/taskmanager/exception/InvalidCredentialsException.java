package com.example.taskmanager.exception;

import com.example.taskmanager.util.ExceptionMessages;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super(ExceptionMessages.INVALID_CREDENTIALS_MSG);
    }
}