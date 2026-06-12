package org.example.course_management.model.dto.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private String studentName;
    private String courseCode;
    private String reportUrl;
    private Double score;
    private String feedback;
    private String status;
}
