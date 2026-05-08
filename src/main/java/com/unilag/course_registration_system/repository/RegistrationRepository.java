package com.unilag.course_registration_system.repository;

import com.unilag.course_registration_system.entity.Registration;
import com.unilag.course_registration_system.entity.Semester;
import com.unilag.course_registration_system.entity.Student;
import com.unilag.course_registration_system.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {
    boolean existsByStudentsAndSemesterAndStatus(Student student, Semester semester, RegistrationStatus registrationStatus);
    Optional<Registration> findFirstByStudentsAndSemester_AcademicSessionOrderByIdDesc(Student student, String academicSession);
}
