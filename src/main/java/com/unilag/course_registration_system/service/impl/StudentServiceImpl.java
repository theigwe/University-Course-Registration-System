package com.unilag.course_registration_system.service.impl;

import com.unilag.course_registration_system.dto.request.CreateStudent;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.entity.Department;
import com.unilag.course_registration_system.entity.Student;
import com.unilag.course_registration_system.exception.NotFoundException;
import com.unilag.course_registration_system.model.StudentModel;
import com.unilag.course_registration_system.repository.DepartmentRepository;
import com.unilag.course_registration_system.repository.StudentRepository;
import com.unilag.course_registration_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;
import static com.unilag.course_registration_system.utils.ResponseCodes.GENERAL_SUCCESS_CODE;
import static com.unilag.course_registration_system.utils.ResponseCodes.VALIDATION_FAILED_CODE;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Response<Void> registerStudent(CreateStudent request) {
        if(request.getStudentId() == null || request.getStudentId().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Student Id is required.");
        }
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "First Name is required.");
        }
        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Last Name is required.");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Email is required.");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Address is required.");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Phone Number is required.");
        }
        if (request.getCurrentLevel() == null || request.getCurrentLevel().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Current Level is required.");
        }
        if (request.getAcademicSession() == null || request.getAcademicSession().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Academic Session is required.");
        }

        Department dept = departmentRepository.findById(request.getDepartmentId()).orElseThrow(()-> new NotFoundException("Department not found"));
        Student student = new Student(generateUniqueId(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber(), request.getAddress(), request.getCurrentLevel(), dept, request.getAcademicSession());
        studentRepository.save(student);
        System.out.println("Student successfully registered.");
        return new Response<>(GENERAL_SUCCESS_CODE, "Student registered successfully");
    }

    @Override
    public Response<StudentModel> getStudent(String studentId) {
        Student std = studentRepository.findByStudentId(studentId).orElseThrow(()-> new NotFoundException("Student not found"));
        StudentModel student = studentRepository.findByStudentIdAndFirstName(studentId, std.getFirstName()).orElseThrow(()-> new NotFoundException("Student profile not found"));
        return new Response<>(GENERAL_SUCCESS_CODE, "Student profile successfully found", student);
    }


    private String generateUniqueId() {
        int year = LocalDate.now().getYear();

        // Find the count of students registered this year to increment the sequence
        long count = studentRepository.countByStudentIdStartingWith("STU-" + year);
        String sequence = String.format("%03d", count + 1);

        // Result example: STU-2025-001
        String newId = "STU-" + year + "-" + sequence;

        // Double check for collisions in high-concurrency environments
        if (studentRepository.existsByStudentId(newId)) {
            return "STU-" + year + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }

        return newId;
    }

}
