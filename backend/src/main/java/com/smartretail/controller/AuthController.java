package com.smartretail.controller;

import com.smartretail.dto.AuthDTOs;
import com.smartretail.entity.User;
import com.smartretail.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> register(@RequestBody AuthDTOs.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> login(@RequestBody AuthDTOs.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = authService.getCurrentUser();
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}
