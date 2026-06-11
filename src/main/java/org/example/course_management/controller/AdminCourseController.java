package org.example.course_management.controller;

import jakarta.validation.Valid;
import org.example.course_management.model.dto.request.CourseRequest;
import org.example.course_management.model.dto.response.ApiResponse;
import org.example.course_management.model.entity.Course;
import org.example.course_management.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<Course>> buildCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.saveCourse(request), "Tạo khóa học thành công."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> indexCourses(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query) {
        
        // 1. Xử lý tìm kiếm
        if (query != null && !query.isBlank()) {
            Pageable pageable = (page != null && size != null) ? PageRequest.of(page, size) : Pageable.unpaged();
            List<Course> results = courseService.searchCourses(query, pageable);
            return ResponseEntity.ok(ApiResponse.success(results, results.isEmpty() ? "Không tìm thấy kết quả phù hợp." : "Kết quả tìm kiếm khóa học."));
        }

        // 2. Xử lý lấy tất cả hoặc phân trang
        List<Course> courses;
        if (page != null && size != null) {
            courses = courseService.fetchAllCourses(PageRequest.of(page, size));
        } else {
            // Trả về toàn bộ danh sách khi không có tham số phân trang
            courses = courseService.fetchAllCourses(Pageable.unpaged());
        }
        
        return ResponseEntity.ok(ApiResponse.success(courses, "Danh sách khóa học."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> modifyCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(id, request), "Cập nhật khóa học thành công."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa khóa học thành công."));
    }
}
