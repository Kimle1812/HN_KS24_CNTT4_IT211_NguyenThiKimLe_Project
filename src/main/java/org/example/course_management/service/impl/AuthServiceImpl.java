package org.example.course_management.service.impl;

import org.example.course_management.config.JwtProvider;
import org.example.course_management.exception.BadCredentialsException;
import org.example.course_management.exception.InvalidStateException;
import org.example.course_management.mapper.UserMapper;
import org.example.course_management.model.dto.request.LoginRequest;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.JwtResponse;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.model.entity.RoleEnum;
import org.example.course_management.model.entity.TokenBlacklist;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.TokenBlacklistRepository;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository blacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập không tồn tại."));
        if (!user.getIsActive()) throw new RuntimeException("Tài khoản đã bị khóa.");
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Mật khẩu không chính xác.");
        }
        return new JwtResponse(jwtProvider.generateAccessToken(user.getUsername(), user.getRole().name()),
                jwtProvider.generateRefreshToken(user.getUsername()));
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) throw new BadCredentialsException("Phiên làm việc đã hết hạn hoặc mã không hợp lệ.");
        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));
        return new JwtResponse(jwtProvider.generateAccessToken(user.getUsername(), user.getRole().name()), refreshToken);
    }

    @Override
    public void invalidateToken(String token) {
        blacklistRepository.save(TokenBlacklist.builder().tokenString(token).revokedAt(LocalDateTime.now()).build());
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) throw new InvalidStateException("Mật khẩu cũ không chính xác.");
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserResponse register(UserCreateRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) throw new RuntimeException("Tên đăng nhập đã tồn tại.");
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
