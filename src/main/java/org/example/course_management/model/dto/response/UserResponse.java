package org.example.course_management.model.dto.response;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private Boolean isActive;
}
