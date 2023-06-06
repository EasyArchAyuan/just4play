package com.example.ayuan.exception;


import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常类
 */
@RestControllerAdvice
public class ThreeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ThreeException() {}

    public ThreeException(String message) {
        super(message);
    }
}
