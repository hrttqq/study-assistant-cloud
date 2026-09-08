package com.study.user.api;

public class UserNoExhaustedException extends RuntimeException{

    public UserNoExhaustedException(String message) {
        super(message);
    }
}
