package com.smartretail.service;

import com.smartretail.dto.AuthDTOs;
import com.smartretail.entity.User;

public interface AuthService {
    AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request);
    AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request);
    User getCurrentUser();
    User getUserById(Long id);
}
