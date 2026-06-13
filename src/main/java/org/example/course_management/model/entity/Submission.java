package org.example.course_management.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id", "assignment_name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "assignment_name", nullable = false, length = 100)
    private String assignmentName;

    @Column(nullable = false, length = 500)
    private String reportUrl;

    private Double score;

    @Column(length = 1000)
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnum status;
}