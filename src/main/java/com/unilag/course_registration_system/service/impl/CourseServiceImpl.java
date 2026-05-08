package com.unilag.course_registration_system.service.impl;

import com.unilag.course_registration_system.dto.request.CreateCourse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.dto.response.TokenValidationResponse;
import com.unilag.course_registration_system.entity.ActiveSemester;
import com.unilag.course_registration_system.entity.Course;
import com.unilag.course_registration_system.entity.Department;
import com.unilag.course_registration_system.entity.Student;
import com.unilag.course_registration_system.exception.NotFoundException;
import com.unilag.course_registration_system.model.CourseModel;
import com.unilag.course_registration_system.repository.ActiveSemesterRepository;
import com.unilag.course_registration_system.repository.CourseRepository;
import com.unilag.course_registration_system.repository.DepartmentRepository;
import com.unilag.course_registration_system.repository.StudentRepository;
import com.unilag.course_registration_system.service.CourseService;
import com.unilag.course_registration_system.session.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.unilag.course_registration_system.utils.ResponseCodes.GENERAL_SUCCESS_CODE;
import static com.unilag.course_registration_system.utils.ResponseCodes.VALIDATION_FAILED_CODE;


@Slf4j
@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final ActiveSemesterRepository activeSemesterRepository;
    private final TokenService tokenService;
    private final StudentRepository studentRepository;
    private final HttpServletRequest servletRequest;

    @Override
    public Response<Void> createCourse(CreateCourse request) {
        if (request.getDepartmentId() == null || request.getDepartmentId() == 0) {
            return new Response<>(VALIDATION_FAILED_CODE, "Department id is required");
        }
        if (request.getCourseCode() == null || request.getCourseCode().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Course code is required");
        }
        if (request.getCourseTitle() == null || request.getCourseTitle().isEmpty()) {
            return new Response<>(VALIDATION_FAILED_CODE, "Course title is required");
        }
        if (request.getSemester() == null || (!request.getSemester().equals("FIRST") && !request.getSemester().equals("SECOND"))) {
            return new Response<>(VALIDATION_FAILED_CODE, "Semester must be FIRST or SECOND");
        }
        Department dept = departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new NotFoundException("Department does not exist"));
        Course course = new Course(request.getCourseCode(), request.getCourseTitle(), request.getCreditUnit(), request.getPrerequisite(), request.getAvailableSlots(), request.getLevel(), request.getSemester(), dept);
        courseRepository.save(course);
        System.out.println("Course created successfully");
        return new Response<>(GENERAL_SUCCESS_CODE, "Course created successfully");
    }

    @Override
    public Response<List<CourseModel>> fetchCourses() {
        String token = tokenService.getToken(servletRequest);
        TokenValidationResponse session = tokenService.validateToken(token);
        log.info("Logged in user {}", session);

        ActiveSemester activeSemester = activeSemesterRepository.findByActiveTrue()
                .orElseThrow(() -> new NotFoundException("Registration is currently closed. No active semester set."));

        Student student = studentRepository.findByStudentId(session.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student record not found"));
        Department dept = departmentRepository.findById(session.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department does not exist"));

        log.info("Active semester: {} {}", activeSemester.getSemesterName(), activeSemester.getAcademicSession());
        List<CourseModel> courses = courseRepository.findByDepartmentAndLevelAndSemester(
                dept, student.getCurrentLevel(), activeSemester.getSemesterName());
        return new Response<>(GENERAL_SUCCESS_CODE, "Courses retrieved successfully", courses);
    }
}
