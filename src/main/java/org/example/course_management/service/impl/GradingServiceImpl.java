package org.example.course_management.service.impl;

import org.example.course_management.exception.InvalidStateException;
import org.example.course_management.mapper.SubmissionMapper;
import org.example.course_management.model.dto.request.GradeRequest;
import org.example.course_management.model.dto.response.SubmissionResponse;
import org.example.course_management.model.entity.Course;
import org.example.course_management.model.entity.StatusEnum;
import org.example.course_management.model.entity.Submission;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.*;
import org.example.course_management.service.GradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubmissionMapper submissionMapper;

    @Override
    public SubmissionResponse submitRepo(String username, String courseCode, String assignmentName, String githubLink) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên."));
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học."));

        submissionRepository.findByStudentIdAndCourseIdAndAssignmentName(student.getId(), course.getId(), assignmentName)
                .ifPresent(submission -> {
                    throw new InvalidStateException("Bạn đã nộp bài tập '" + assignmentName + "' cho khóa học này rồi.");
                });

        Submission submission = Submission.builder()
                .student(student)
                .course(course)
                .assignmentName(assignmentName)
                .reportUrl(githubLink)
                .status(StatusEnum.SUBMITTED)
                .build();

        return submissionMapper.toSubmissionResponse(submissionRepository.save(submission));
    }

    @Override
    public SubmissionResponse executeGrading(GradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp."));

        if (submission.getStatus() == StatusEnum.PENDING) {
            throw new InvalidStateException("Bài tập của sinh viên đang ở trạng thái chờ, không thể chấm điểm.");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(StatusEnum.GRADED);
        return submissionMapper.toSubmissionResponse(submissionRepository.save(submission));
    }

    @Override
    public void bindMaterials(String courseCode, String url) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();
        course.setLectureMaterialsUrl(url);
        courseRepository.save(course);
    }

    @Override
    public List<SubmissionResponse> fetchAllSubmissions() {
        List<Submission> submissions = submissionRepository.findAll();
        return submissions.stream()
                .map(submissionMapper::toSubmissionResponse)
                .toList();
    }

    @Override
    public List<SubmissionResponse> fetchSubmissionsByStudent(String username) {
        List<Submission> submissions = submissionRepository.findByStudent_Username(username);
        return submissions.stream()
                .map(submissionMapper::toSubmissionResponse)
                .toList();
    }
}
