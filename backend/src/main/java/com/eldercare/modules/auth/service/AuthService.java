package com.eldercare.modules.auth.service;

import com.eldercare.modules.auth.dto.request.LoginRequest;
import com.eldercare.modules.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void resetPassword(String email);
}
