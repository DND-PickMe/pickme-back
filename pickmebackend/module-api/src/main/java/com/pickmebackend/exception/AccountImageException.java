package com.pickmebackend.exception;

/**
 * Reference
 * https://github.com/spring-guides/gs-uploading-files
 */
public class AccountImageException extends RuntimeException {

    public AccountImageException(String message) {
        super(message);
    }

    public AccountImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
