package org.example.course_management.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {

    @NotBlank(message = "Mã khóa học không được để trống")
    private String courseCode;

    @NotBlank(message = "Link GitHub không được để trống")
    private String repoUrl;
}
