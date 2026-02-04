package com.collaborative.editor.controller;

import com.collaborative.editor.dto.LoginRequest;
import com.collaborative.editor.dto.LoginResponse;
import com.collaborative.editor.exception.AuthenticationFailedException;
import com.collaborative.editor.exception.UserNotFoundException;
import com.collaborative.editor.service.interfaces.AuthService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class LogInController {

    private final AuthService authService;

    public LogInController(@Qualifier("AuthServiceImpl") AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.verify(loginRequest);
            return buildResponse(token, "Login successful", HttpStatus.OK);
        } catch (AuthenticationFailedException e) {
            return buildResponse(null, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException e) {
            return buildResponse(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildResponse(null, "Login failed, please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ResponseEntity<LoginResponse> buildResponse(String token, String message, HttpStatus status) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setMessage(message);
        return new ResponseEntity<>(response, status);
    }
}
