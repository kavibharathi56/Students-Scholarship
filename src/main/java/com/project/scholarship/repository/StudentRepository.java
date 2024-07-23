package com.project.scholarship.repository;

import com.project.scholarship.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumberAndDob(String registrationNumber, LocalDate dob);
}
