package com.zhvavyy.backend.web.handler;

public class UsernameAlreadyException extends RuntimeException {
    public UsernameAlreadyException(String message) {
        super(message);
    }
}
