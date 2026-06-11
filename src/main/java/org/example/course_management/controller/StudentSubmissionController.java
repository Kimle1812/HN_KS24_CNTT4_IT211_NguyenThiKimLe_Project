package org.example.course_management.controller;

import org.example.course_management.model.dto.request.EnrollmentRequest;
import org.example.course_management.model.dto.request.SubmissionRequest;
import org.example.course_management.model.dto.response.ApiResponse;
import org.example.course_management.model.dto.response.SubmissionResponse;
import org.example.course_management.model.entity.Course;
import java.util.List;
import org.example.course_management.service.CourseService;
import org.example.course_management.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentSubmissionController {

    private final CourseService courseService;
    private final GradingService gradingService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> listEnrolledCourses() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Course> courses = courseService.fetchEnrolledCourses(user);
        return ResponseEntity.ok(ApiResponse.success(courses, "Danh sách khóa học đã đăng ký."));
    }

    @PostMapping("/courses/enroll")
    public ResponseEntity<ApiResponse<Void>> requestEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        courseService.enrollStudent(user, request.getCourseCode());
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng ký khóa học thành công."));
    }

    @PostMapping("/submissions")
    public ResponseEntity<ApiResponse<SubmissionResponse>> handInProject(@Valid @RequestBody SubmissionRequest request) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        SubmissionResponse res = gradingService.submitRepo(user, request.getCourseCode(), request.getRepoUrl());
        return ResponseEntity.ok(ApiResponse.success(res, "Nộp bài tập thành công."));
    }
}
