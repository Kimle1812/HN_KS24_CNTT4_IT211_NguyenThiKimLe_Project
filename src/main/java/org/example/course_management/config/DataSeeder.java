package org.example.course_management.config;

import lombok.RequiredArgsConstructor;
import org.example.course_management.model.entity.Course;
import org.example.course_management.model.entity.RoleEnum;
import org.example.course_management.model.entity.User;
import org.example.course_management.repository.CourseRepository;
import org.example.course_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            Course c1 = Course.builder()
                    .courseCode("JAVA01")
                    .courseName("Java Core Basics")
                    .credit(3)
                    .build();

            Course c2 = Course.builder()
                    .courseCode("WEB02")
                    .courseName("Web Development with Spring")
                    .credit(4)
                    .build();

            courseRepository.saveAll(Arrays.asList(c1, c2));
            System.out.println("Sample courses seeded to database.");
        }

        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(RoleEnum.ADMIN)
                    .isActive(true)
                    .build();

            User lecturer = User.builder()
                    .username("lecturer")
                    .passwordHash(passwordEncoder.encode("lecturer123"))
                    .role(RoleEnum.LECTURER)
                    .isActive(true)
                    .build();

            User student = User.builder()
                    .username("student")
                    .passwordHash(passwordEncoder.encode("student123"))
                    .role(RoleEnum.STUDENT)
                    .isActive(true)
                    .build();

            userRepository.saveAll(Arrays.asList(admin, lecturer, student));
            System.out.println("Sample users seeded to database (admin/admin123, lecturer/lecturer123, student/student123).");
        }
    }
}
