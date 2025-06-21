package com.collaborative.editor.exception;

public class RepositoryUpdateFailedException extends RuntimeException {
    public RepositoryUpdateFailedException(String message) {
        super(message);
    }
}
