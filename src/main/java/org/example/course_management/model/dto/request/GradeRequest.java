package org.example.course_management.model.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GradeRequest {
    @NotNull(message = "Submission ID is required")
    private Long submissionId;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Double score;

    @Size(max = 1000, message = "Feedback maximum 1000 characters")
    private String feedback;
}
