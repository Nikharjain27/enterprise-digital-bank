package com.bank.auth.service;

import com.bank.auth.dto.AuthResponse;
import com.bank.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
}