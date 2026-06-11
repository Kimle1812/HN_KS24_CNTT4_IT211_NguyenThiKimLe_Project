package org.example.course_management.service;

import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    UserResponse createNewUser(UserCreateRequest request);
    List<UserResponse> fetchAllUsers(Pageable pageable);
    UserResponse updateUserInfo(Long id, UserCreateRequest request);
    void deleteUser(Long id);
    List<UserResponse> searchUsers(String query, Pageable pageable);
}
