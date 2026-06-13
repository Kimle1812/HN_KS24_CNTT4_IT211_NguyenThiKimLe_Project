package org.example.course_management.service;

import org.example.course_management.exception.InvalidStateException;
import org.example.course_management.model.entity.Course;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.CourseRepository;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;

    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepository, userRepository);
    }

    @Test
    void enrollStudent_Success() {
        User student = User.builder().id(1L).username("student").build();
        Course course = Course.builder()
                .id(1L)
                .courseCode("JAVA01")
                .students(new HashSet<>())
                .build();

        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(courseRepository.findByCourseCode("JAVA01")).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        courseService.enrollStudent("student", "JAVA01");

        assertTrue(course.getStudents().contains(student));
        verify(courseRepository).save(course);
    }

    @Test
    void enrollStudent_Duplicate_ThrowsInvalidState() {
        User student = User.builder().id(1L).username("student").build();
        Course course = Course.builder()
                .id(1L)
                .courseCode("JAVA01")
                .students(new HashSet<>())
                .build();
        course.getStudents().add(student);

        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(courseRepository.findByCourseCode("JAVA01")).thenReturn(Optional.of(course));

        assertThrows(InvalidStateException.class, () -> courseService.enrollStudent("student", "JAVA01"));
        verify(courseRepository, never()).save(any());
    }
}
