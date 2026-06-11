package org.example.course_management.repository;
import org.example.course_management.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
