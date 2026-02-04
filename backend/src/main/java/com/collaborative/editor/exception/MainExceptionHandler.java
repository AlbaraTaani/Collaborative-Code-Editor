package com.collaborative.editor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MainExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MainExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "User Not Found",
                ex.getMessage(),
                "USER_NOT_FOUND");
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Authentication Failed",
                "The credentials you provided are invalid. Please log in again.",
                "AUTHENTICATION_FAILED");
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleProjectNotFoundException(ProjectNotFoundException ex) {
        logger.error("Project not found: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Project Not Found",
                "The specified project does not exist.",
                "PROJECT_NOT_FOUND");
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFileNotFoundException(FileNotFoundException ex) {
        logger.error("File not found: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "File Not Found",
                "The requested file could not be found.",
                "FILE_NOT_FOUND");
    }

    @ExceptionHandler(FileConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleFileConflictException(FileConflictException ex) {
        logger.warn("File conflict: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "File Conflict",
                "A file with that name already exists in the target directory.",
                "FILE_CONFLICT");
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRepositoryNotFoundException(RepositoryNotFoundException ex) {
        logger.error("Repository not found: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Repository Not Found",
                "The requested repository does not exist.",
                "REPOSITORY_NOT_FOUND");
    }

    @ExceptionHandler(RepositoryUpdateFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRepositoryUpdateFailedException(RepositoryUpdateFailedException ex) {
        logger.warn("Repository update failed: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Repository Update Failed",
                "Failed to update the repository. Please verify your changes and try again.",
                "REPOSITORY_UPDATE_FAILED");
    }

    @ExceptionHandler(RepositoryCreationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRepositoryCreationFailedException(RepositoryCreationFailedException ex) {
        logger.error("Repository creation failed: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Repository Creation Failed",
                "We couldn’t create your repository. Please check your permissions and try again.",
                "REPOSITORY_CREATION_FAILED");
    }

    @ExceptionHandler(RepositoryDeletionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRepositoryDeletionFailedException(RepositoryDeletionFailedException ex) {
        logger.warn("Repository deletion failed: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Repository Deletion Failed",
                "Unable to delete the repository. It may have already been removed or you lack permissions.",
                "REPOSITORY_DELETION_FAILED");
    }

    @ExceptionHandler(RepositoryMembershipNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRepositoryMembershipNotFoundException(RepositoryMembershipNotFoundException ex) {
        logger.warn("Repository membership not found: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Repository Membership Not Found",
                "The specified repository membership could not be located.",
                "REPOSITORY_MEMBERSHIP_NOT_FOUND");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception ex) {
        logger.error("An internal server error occurred: {}", ex.getMessage(), ex);
        return createErrorResponse(
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR");
    }

    private Map<String, String> createErrorResponse(String error, String message, String code) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("code", code);
        return errorResponse;
    }
}
