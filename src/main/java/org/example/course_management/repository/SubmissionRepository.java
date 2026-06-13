package org.example.course_management.repository;
import org.example.course_management.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentIdAndCourseId(Long studentId, Long courseId);
    Optional<Submission> findByStudentIdAndCourseIdAndAssignmentName(Long studentId, Long courseId, String assignmentName);
    List<Submission> findByStudent_Username(String username);
}
