package org.example.course_management.mapper;


import org.example.course_management.model.dto.response.SubmissionResponse;
import org.example.course_management.model.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "studentName", source = "student.username")
    @Mapping(target = "courseCode", source = "course.courseCode")
    SubmissionResponse toSubmissionResponse(Submission submission);
}
