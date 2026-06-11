package org.example.course_management.service;


import org.example.course_management.model.dto.request.LoginRequest;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.JwtResponse;
import org.example.course_management.model.dto.response.UserResponse;

public interface AuthService {
    JwtResponse authenticate(LoginRequest request);
    JwtResponse refresh(String refreshToken);
    void invalidateToken(String token);
    void updatePassword(String username, String oldPassword, String newPassword);
    UserResponse register(UserCreateRequest request);
}
