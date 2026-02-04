package com.collaborative.editor.service.interfaces;


import com.collaborative.editor.dto.LoginRequest;

public interface AuthService {
    String verify(LoginRequest loginRequest);
}
