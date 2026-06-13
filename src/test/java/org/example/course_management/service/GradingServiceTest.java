package org.example.course_management.service;

import org.example.course_management.exception.InvalidStateException;
import org.example.course_management.mapper.SubmissionMapper;
import org.example.course_management.model.dto.request.GradeRequest;
import org.example.course_management.model.dto.response.SubmissionResponse;
import org.example.course_management.model.entity.*;
import org.example.course_management.repository.CourseRepository;
import org.example.course_management.repository.SubmissionRepository;
import org.example.course_management.repository.UserRepository;
import org.example.course_management.service.impl.GradingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SubmissionMapper submissionMapper;

    private GradingServiceImpl gradingService;

    @BeforeEach
    void setUp() {
        gradingService = new GradingServiceImpl(submissionRepository, userRepository, courseRepository, submissionMapper);
    }

    @Test
    void submitRepo_Success_ReturnsSubmissionResponse() {
        User student = User.builder().id(1L).username("student").build();
        Course course = Course.builder().id(1L).courseCode("JAVA01").build();
        Submission savedSubmission = Submission.builder()
                .id(1L)
                .student(student)
                .course(course)
                .assignmentName("Bài tập 1")
                .reportUrl("https://github.com/student/repo")
                .status(StatusEnum.SUBMITTED)
                .build();
        SubmissionResponse expectedResponse = SubmissionResponse.builder()
                .id(1L)
                .studentName("student")
                .courseCode("JAVA01")
                .assignmentName("Bài tập 1")
                .reportUrl("https://github.com/student/repo")
                .status("SUBMITTED")
                .build();

        when(userRepository.findByUsername("student")).thenReturn(Optional.of(student));
        when(courseRepository.findByCourseCode("JAVA01")).thenReturn(Optional.of(course));
        when(submissionRepository.findByStudentIdAndCourseIdAndAssignmentName(1L, 1L, "Bài tập 1"))
                .thenReturn(Optional.empty());
        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);
        when(submissionMapper.toSubmissionResponse(savedSubmission)).thenReturn(expectedResponse);

        SubmissionResponse actual = gradingService.submitRepo("student", "JAVA01", "Bài tập 1", "https://github.com/student/repo");

        assertNotNull(actual);
        assertEquals("student", actual.getStudentName());
        assertEquals("JAVA01", actual.getCourseCode());
        assertEquals("Bài tập 1", actual.getAssignmentName());
        assertEquals("SUBMITTED", actual.getStatus());
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void executeGrading_Success_ReturnsGradedResponse() {
        User student = User.builder().id(1L).username("student").build();
        Course course = Course.builder().id(1L).courseCode("JAVA01").build();
        Submission submission = Submission.builder()
                .id(1L)
                .student(student)
                .course(course)
                .assignmentName("Bài tập 1")
                .reportUrl("https://github.com/student/repo")
                .status(StatusEnum.SUBMITTED)
                .build();
        GradeRequest request = new GradeRequest(1L, 85.0, "Bài làm tốt.");
        Submission savedSubmission = Submission.builder()
                .id(1L)
                .student(student)
                .course(course)
                .assignmentName("Bài tập 1")
                .reportUrl("https://github.com/student/repo")
                .score(85.0)
                .feedback("Bài làm tốt.")
                .status(StatusEnum.GRADED)
                .build();
        SubmissionResponse expectedResponse = SubmissionResponse.builder()
                .id(1L)
                .studentName("student")
                .courseCode("JAVA01")
                .assignmentName("Bài tập 1")
                .reportUrl("https://github.com/student/repo")
                .score(85.0)
                .feedback("Bài làm tốt.")
                .status("GRADED")
                .build();

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);
        when(submissionMapper.toSubmissionResponse(savedSubmission)).thenReturn(expectedResponse);

        SubmissionResponse actual = gradingService.executeGrading(request);

        assertNotNull(actual);
        assertEquals(85.0, actual.getScore());
        assertEquals("Bài làm tốt.", actual.getFeedback());
        assertEquals("GRADED", actual.getStatus());
    }

    @Test
    void executeGrading_PendingSubmission_ThrowsInvalidState() {
        Submission submission = Submission.builder()
                .id(1L)
                .status(StatusEnum.PENDING)
                .build();
        GradeRequest request = new GradeRequest(1L, 75.0, "Feedback");

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        assertThrows(InvalidStateException.class, () -> gradingService.executeGrading(request));
        verify(submissionRepository, never()).save(any());
    }
}
