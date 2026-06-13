package org.example.course_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.course_management.model.dto.request.LoginRequest;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.JwtResponse;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void login_ValidRequest_ReturnsJwtResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("mockAccessToken")
                .refreshToken("mockRefreshToken")
                .build();

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công."))
                .andExpect(jsonPath("$.data.accessToken").value("mockAccessToken"));
    }

    @Test
    void login_InvalidRequest_ReturnsBadRequest() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest("", "password123"); // Username rỗng

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_ValidRequest_ReturnsUserResponse() throws Exception {
        UserCreateRequest registerRequest = new UserCreateRequest("newuser", "newpassword", "STUDENT");
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("newuser")
                .role("STUDENT")
                .isActive(true)
                .build();

        when(authService.register(any(UserCreateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated()) // HttpStatus.CREATED
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng ký tài khoản thành công."))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {

        UserCreateRequest invalidRegisterRequest = new UserCreateRequest("us", "pass", "STUDENT"); // Username và password quá ngắn

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void refresh_ValidRefreshToken_ReturnsJwtResponse() throws Exception {
        String refreshToken = "validRefreshToken";
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken(refreshToken)
                .build();

        when(authService.refresh(refreshToken)).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Làm mới token thành công."))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"));
    }

}
