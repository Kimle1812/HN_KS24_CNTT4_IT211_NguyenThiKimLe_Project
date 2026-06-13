package org.example.course_management.controller;

import org.example.course_management.model.dto.request.LoginRequest;
import org.example.course_management.model.dto.request.PasswordChangeRequest;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.ApiResponse;
import org.example.course_management.model.dto.response.JwtResponse;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //{ "username": "admin_new", "password": "admin123" }
    //{ "username": "student01", "password": "student123" }
    //{ "username": "lecturer01", "password": "lecturer123" }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.authenticate(request), "Đăng nhập thành công."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authService.register(request), "Đăng ký tài khoản thành công."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(refreshToken), "Làm mới token thành công."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            authService.invalidateToken(bearer.substring(7));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công."));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        String identity = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updatePassword(identity, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công."));
    }
}
