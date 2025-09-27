package com.zhvavyy.backend.exception;

public class UserNotFoundCustomException extends RuntimeException {
    public UserNotFoundCustomException(String message) {
        super(message);
    }
}
