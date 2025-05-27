package com.nhnacademy.ruleengineservice.exception.auth;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
