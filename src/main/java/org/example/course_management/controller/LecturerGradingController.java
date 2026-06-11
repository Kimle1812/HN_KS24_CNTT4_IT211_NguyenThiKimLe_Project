package org.example.course_management.controller;

import org.example.course_management.model.dto.request.GradeRequest;
import org.example.course_management.model.dto.response.ApiResponse;
import org.example.course_management.model.dto.response.SubmissionResponse;
import org.example.course_management.service.CloudStorageService;
import org.example.course_management.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LecturerGradingController {

    private final GradingService gradingService;
    private final CloudStorageService storageService;

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<SubmissionResponse>> recordGrade(@Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gradingService.executeGrading(request), "Chấm điểm đồ án thành công."));
    }

    @PostMapping("/materials")
    public ResponseEntity<ApiResponse<String>> syncMaterials(@RequestParam String courseCode, @RequestParam("file") MultipartFile file) {
        String cloudAssetUrl = storageService.uploadFile(file);
        gradingService.bindMaterials(courseCode, cloudAssetUrl);
        return ResponseEntity.ok(ApiResponse.success(cloudAssetUrl, "Tải lên tài liệu bài giảng thành công."));
    }
}
