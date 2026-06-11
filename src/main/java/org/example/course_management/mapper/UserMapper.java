package org.example.course_management.mapper;

import org.example.course_management.model.dto.response.UserResponse;
import org.example.course_management.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
