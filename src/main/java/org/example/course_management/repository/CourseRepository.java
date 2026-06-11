package org.example.course_management.repository;

import org.example.course_management.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    Page<Course> findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(String name, String code, Pageable pageable);
    List<Course> findByStudents_Username(String username);
}
