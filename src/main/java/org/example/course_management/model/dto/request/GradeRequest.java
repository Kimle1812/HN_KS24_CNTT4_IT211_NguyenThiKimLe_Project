package org.example.course_management.model.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GradeRequest {
    @NotNull(message = "Mã bài nộp không được để trống")
    private Long submissionId;

    @Min(value = 0, message = "Điểm phải lớn hơn hoặc bằng 0")
    @Max(value = 100, message = "Điểm không được vượt quá 100")
    private Double score;

    @Size(max = 1000, message = "Nhận xét tối đa 1000 ký tự")
    private String feedback;
}
