package org.example.course_management.service;


import org.example.course_management.model.dto.request.GradeRequest;
import org.example.course_management.model.dto.response.SubmissionResponse;

public interface GradingService {
    SubmissionResponse submitRepo(String username, String courseCode, String githubLink);
    SubmissionResponse executeGrading(GradeRequest request);
    void bindMaterials(String courseCode, String url);
}
