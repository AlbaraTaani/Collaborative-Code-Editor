package com.collaborative.editor.exception;

public class RepositoryDeletionFailedException extends RuntimeException {
  public RepositoryDeletionFailedException(String message) {
    super(message);
  }
}
