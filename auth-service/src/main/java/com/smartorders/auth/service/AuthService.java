package com.smartorders.auth.service;

import com.smartorders.auth.dto.AuthResponse;
import com.smartorders.auth.dto.LoginRequest;
import com.smartorders.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}