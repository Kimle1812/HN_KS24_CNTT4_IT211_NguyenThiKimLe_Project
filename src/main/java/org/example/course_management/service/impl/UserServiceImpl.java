package org.example.course_management.service.impl;

import org.example.course_management.mapper.UserMapper;
import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.model.entity.RoleEnum;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse createNewUser(UserCreateRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) throw new RuntimeException("Username conflict.");
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.valueOf(request.getRole().toUpperCase()))
                .isActive(true)
                .build();
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> fetchAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUserInfo(Long id, UserCreateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(RoleEnum.valueOf(request.getRole().toUpperCase()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> searchUsers(String query, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(query, pageable).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
