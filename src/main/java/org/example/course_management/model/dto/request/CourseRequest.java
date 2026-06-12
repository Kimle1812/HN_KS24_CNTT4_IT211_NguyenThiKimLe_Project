package org.example.course_management.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CourseRequest {
    @NotBlank(message = "Mã khóa học không được để trống")
    private String courseCode;

    @NotBlank(message = "Tên khóa học không được để trống")
    private String courseName;

    @Min(value = 1, message = "Số tín chỉ phải lớn hơn hoặc bằng 1")
    private Integer credit;

    private String description;
}
