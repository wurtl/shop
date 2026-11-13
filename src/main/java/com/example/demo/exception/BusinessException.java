package com.example.demo.exception;

/** Нарушено бизнес-правило (HTTP 400). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
