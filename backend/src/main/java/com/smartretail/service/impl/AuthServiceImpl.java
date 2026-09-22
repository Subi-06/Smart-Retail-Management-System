package com.smartretail.service.impl;

import com.smartretail.dto.AuthDTOs;
import com.smartretail.entity.Cart;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.MembershipType;
import com.smartretail.entity.enums.Role;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.CartRepository;
import com.smartretail.repository.UserRepository;
import com.smartretail.security.JwtTokenProvider;
import com.smartretail.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email '" + request.getEmail() + "' already exists.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .membershipType(request.getMembershipType() != null ? request.getMembershipType() : MembershipType.REGULAR)
                .phone(request.getPhone())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        // Initialize empty shopping cart for new customer
        Cart cart = Cart.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        cartRepository.save(cart);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return AuthDTOs.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .membershipType(user.getMembershipType())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return AuthDTOs.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .membershipType(user.getMembershipType())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
