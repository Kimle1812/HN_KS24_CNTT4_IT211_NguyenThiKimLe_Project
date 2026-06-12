package org.example.course_management.service.impl;

import org.example.course_management.model.dto.request.CourseRequest;
import org.example.course_management.model.entity.Course;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.CourseRepository;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public Course saveCourse(CourseRequest request) {
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credit(request.getCredit())
                .build();
        return courseRepository.save(course);
    }

    @Override
    public List<Course> fetchAllCourses(Pageable pageable) { return courseRepository.findAll(pageable).getContent(); }

    @Override
    public void enrollStudent(String username, String courseCode) {
        User student = userRepository.findByUsername(username).orElseThrow();
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();
        if (course.getStudents() == null) course.setStudents(new HashSet<>());
        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Override
    public List<Course> fetchEnrolledCourses(String username) {
        return courseRepository.findByStudents_Username(username);
    }

    @Override
    public Course updateCourse(Long id, CourseRequest request) {
        Course existing = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học."));
        existing.setCourseCode(request.getCourseCode());
        existing.setCourseName(request.getCourseName());
        existing.setCredit(request.getCredit());
        return courseRepository.save(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> searchCourses(String query, Pageable pageable) {
        return courseRepository.findByCourseNameContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(query, query, pageable).getContent();
    }
}
