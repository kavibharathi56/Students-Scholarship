package com.project.scholarship.repository;

import com.project.scholarship.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumberAndDob(String registrationNumber, LocalDate dob);

    List<Student> findByScholarshipName(String scholarshipName);

    long countByScholarshipNameIsNotNull();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.department = :department")
    long countByDepartment(@Param("department") String department);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.scholarshipName = :scholarshipName")
    long countByScholarshipName(@Param("scholarshipName") String scholarshipName);

    @Query("SELECT DISTINCT s.department FROM Student s")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT s.scholarshipName FROM Student s")
    List<String> findDistinctScholarshipNames();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.scholarshipName = :scheme")
    long countByScheme(@Param("scheme") String scheme);



}
