package com.collaborative.editor.exception;

public class FileConflictException extends RuntimeException {
    public FileConflictException(String message) {
        super(message);
    }
}
