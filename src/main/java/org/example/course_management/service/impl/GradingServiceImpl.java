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

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubmissionMapper submissionMapper;

    @Override
    public SubmissionResponse submitRepo(String username, String courseCode, String githubLink) {
        User student = userRepository.findByUsername(username).orElseThrow();
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();

        Submission submission = submissionRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElse(Submission.builder().student(student).course(course).status(StatusEnum.PENDING).build());

        // Tuân thủ sơ đồ chuyển dịch vòng đời trạng thái (PENDING/GRADED -> SUBMITTED)
        submission.setReportUrl(githubLink);
        submission.setStatus(StatusEnum.SUBMITTED);
        return submissionMapper.toSubmissionResponse(submissionRepository.save(submission));
    }

    @Override
    public SubmissionResponse executeGrading(GradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission record missing."));

        if (submission.getStatus() == StatusEnum.PENDING) {
            throw new InvalidStateException("Student project is pending. Cannot grade empty slate.");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(StatusEnum.GRADED); // Trạng thái chuyển dịch vòng đời cuối cùng: GRADED
        return submissionMapper.toSubmissionResponse(submissionRepository.save(submission));
    }

    @Override
    public void bindMaterials(String courseCode, String url) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();
        course.setLectureMaterialsUrl(url);
        courseRepository.save(course);
    }
}
