package com.collaborative.editor.exception;

public class RepositoryNotFoundException extends RuntimeException {
    public RepositoryNotFoundException(String message) {
        super(message);
    }
}
