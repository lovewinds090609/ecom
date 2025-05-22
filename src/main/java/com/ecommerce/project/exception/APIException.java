package com.ecommerce.project.exception;

/**
 *
 * 拋出API錯誤
 *
 */
public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
