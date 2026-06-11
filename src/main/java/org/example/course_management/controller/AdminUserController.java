package org.example.course_management.controller;

import org.example.course_management.model.dto.request.UserCreateRequest;
import org.example.course_management.model.dto.response.ApiResponse;
import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> registerAccount(@Valid @RequestBody UserCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.createNewUser(req), "Tạo tài khoản người dùng thành công."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> indexUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        
        if (query != null && !query.isBlank()) {
            List<UserResponse> results = userService.searchUsers(query, PageRequest.of(page, size));
            if (results.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(results, "Không tìm thấy người dùng nào phù hợp với từ khóa '" + query + "'."));
            }
            return ResponseEntity.ok(ApiResponse.success(results, "Tìm thấy " + results.size() + " người dùng phù hợp."));
        }
        
        return ResponseEntity.ok(ApiResponse.success(userService.fetchAllUsers(PageRequest.of(page, size)), "Danh sách người dùng hiện tại."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateAccount(@PathVariable Long id, @Valid @RequestBody UserCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserInfo(id, req), "Cập nhật thông tin người dùng thành công."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeAccount(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa người dùng thành công."));
    }
}
