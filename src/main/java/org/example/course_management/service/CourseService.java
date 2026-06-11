package org.example.course_management.service;

import org.example.course_management.model.dto.request.CourseRequest;
import org.example.course_management.model.entity.Course;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {
    Course saveCourse(CourseRequest request);
    List<Course> fetchAllCourses(Pageable pageable);
    void enrollStudent(String username, String courseCode);
    List<Course> fetchEnrolledCourses(String username);
    Course updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);
    List<Course> searchCourses(String query, Pageable pageable);
}
