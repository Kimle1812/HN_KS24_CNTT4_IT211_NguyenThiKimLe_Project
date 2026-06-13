package org.example.course_management.service;

import org.example.course_management.config.JwtProvider;
import org.example.course_management.exception.BadCredentialsException;
import org.example.course_management.mapper.UserMapper;
import org.example.course_management.model.dto.request.LoginRequest;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.JwtResponse;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.model.entity.RoleEnum;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.TokenBlacklistRepository;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenBlacklistRepository blacklistRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserMapper userMapper;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, blacklistRepository, passwordEncoder, jwtProvider, userMapper);
    }

    @Test
    void register_Success_ReturnsUserResponse() {
        UserCreateRequest request = new UserCreateRequest("newuser", "password123", "STUDENT");
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .passwordHash("encodedPassword")
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();
        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .username("newuser")
                .role("STUDENT")
                .isActive(true)
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse actual = authService.register(request);

        assertNotNull(actual);
        assertEquals("newuser", actual.getUsername());
        assertEquals("STUDENT", actual.getRole());
        assertTrue(actual.getIsActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest("existing", "password123", "STUDENT");
        User existingUser = User.builder().username("existing").build();

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(exception.getMessage().contains("Tên đăng nhập đã tồn tại"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_Success_ReturnsJwtResponse() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = User.builder()
                .username("testuser")
                .passwordHash("encodedPassword")
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateAccessToken("testuser", "STUDENT")).thenReturn("accessToken");
        when(jwtProvider.generateRefreshToken("testuser")).thenReturn("refreshToken");

        JwtResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void authenticate_WrongPassword_ThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        User user = User.builder()
                .username("testuser")
                .passwordHash("encodedPassword")
                .role(RoleEnum.STUDENT)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }
}
