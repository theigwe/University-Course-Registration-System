package com.unilag.course_registration_system.service.impl;

import com.unilag.course_registration_system.dto.request.RegistrationRequest;
import com.unilag.course_registration_system.dto.response.RegistrationStatusResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.dto.response.TokenValidationResponse;
import com.unilag.course_registration_system.model.CourseModel;
import com.unilag.course_registration_system.entity.ActiveSemester;
import com.unilag.course_registration_system.entity.Course;
import com.unilag.course_registration_system.entity.Department;
import com.unilag.course_registration_system.entity.Registration;
import com.unilag.course_registration_system.entity.Student;
import com.unilag.course_registration_system.exception.NotFoundException;
import com.unilag.course_registration_system.model.RegistrationStatus;
import com.unilag.course_registration_system.repository.ActiveSemesterRepository;
import com.unilag.course_registration_system.repository.CourseRepository;
import com.unilag.course_registration_system.repository.DepartmentRepository;
import com.unilag.course_registration_system.repository.RegistrationRepository;
import com.unilag.course_registration_system.repository.StudentRepository;
import com.unilag.course_registration_system.service.RegistrationService;
import com.unilag.course_registration_system.session.TokenService;
import com.unilag.course_registration_system.utils.ResponseCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import static com.unilag.course_registration_system.model.RegistrationStatus.COMPLETED;
import static com.unilag.course_registration_system.model.RegistrationStatus.PENDING;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final ActiveSemesterRepository activeSemesterRepository;
    private final TokenService tokenService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final HttpServletRequest servletRequest;

    @Override
    @Transactional
    public Response<Void> courseRegistration(RegistrationRequest request) {
        // 1. Authenticate and Fetch Student
        String token = tokenService.getToken(servletRequest);
        TokenValidationResponse session = tokenService.validateToken(token);

        Student student = studentRepository.findByStudentId(session.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        // 2. Validate active semester
        ActiveSemester activeSemester = activeSemesterRepository.findByActiveTrue()
                .orElseThrow(() -> new NotFoundException("Registration is currently closed. No active semester set."));

        if (!activeSemester.getSemesterName().equals(request.getSemester().getSemesterName())) {
            String label = "FIRST".equals(activeSemester.getSemesterName()) ? "First Semester" : "Second Semester";
            throw new NotFoundException("Registration is only open for the " + label + " (" + activeSemester.getAcademicSession() + ").");
        }

        // 3. Validate student session is eligible (student session year >= active session year)
        int studentYear = parseSessionYear(student.getAcademicSession());
        int activeYear  = parseSessionYear(activeSemester.getAcademicSession());
        if (studentYear < activeYear) {
            throw new NotFoundException(
                    "Your academic session (" + student.getAcademicSession() + ") is not eligible for the current registration period (" + activeSemester.getAcademicSession() + ").");
        }

        // 4. Check if already registered for this semester
        boolean alreadyRegistered = registrationRepository.existsByStudentsAndSemesterAndStatus(
                student, request.getSemester(), COMPLETED);

        if (alreadyRegistered) {
            throw new NotFoundException(
                    "Your courses are already registered for the " + request.getSemester().getDisplayLabel());
        }
        Department department = departmentRepository.findById(student.getDepartments().getId()).orElseThrow(()-> new NotFoundException("Department not found"));

        // 3. Fetch and Validate Courses
        List<Course> departmentalCourses = courseRepository.findByLevelAndDepartmentAndSemester(student.getCurrentLevel(), department, request.getSemester().getSemesterName());
        System.out.println("Courses "+ departmentalCourses);
        System.out.println("Departmental Courses "+ departmentalCourses.size());
        System.out.println("Submitted Courses "+ request.getCourseIds().size());

        if (departmentalCourses.size() != request.getCourseIds().size()) {
            throw new NotFoundException("Selected courses does not equal to required courses");
        }

        // 4. OOP Business Validations
        validateBusinessRules(student, departmentalCourses);

        // 5. Create Registration Record
        Registration registration = new Registration();
        registration.setStudents(student);
        registration.setCourses(departmentalCourses);
        registration.setSemester(request.getSemester());
        registration.setStatus(COMPLETED);

        // 6. Update Course Slots (Decrement)
        departmentalCourses.forEach(course -> {
            course.setAvailableSlots(course.getAvailableSlots() - 1);
            courseRepository.save(course);
        });
        registrationRepository.save(registration);
        System.out.println("Courses registered successfully");

        return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE,"Registration successful for the " + request.getSemester().getDisplayLabel());
    }

    @Override
    public Response<RegistrationStatusResponse> checkRegistrationStatus() {
        String token = tokenService.getToken(servletRequest);
        TokenValidationResponse session = tokenService.validateToken(token);

        Student student = studentRepository.findByStudentId(session.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));
        Optional<Registration> reg = registrationRepository.findFirstByStudentsAndSemester_AcademicSessionOrderByIdDesc(student, student.getAcademicSession());
        if (reg.isEmpty()) {
            RegistrationStatusResponse registration = getRegistrationStatusResponse(student);
            return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE,"Registration status retried successfully",registration);
        }
        if (reg.get().getStatus() == COMPLETED) {
            RegistrationStatusResponse registration = getStatusResponse(student, reg.get());
            return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE,"Registration status retrieved successfully",registration);
        }else{
            RegistrationStatusResponse registration = new RegistrationStatusResponse();
            registration.setStatus(PENDING);
            registration.setMessage("Your courses registration is in progress.");
            registration.setRegistered(false);
            registration.setSemesterName(reg.get().getSemester().getSemesterName());
            registration.setAcademicSession(reg.get().getSemester().getAcademicSession());
            registration.setSummary("Course registration in progress");
            return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE,"Registration status successfully",registration);
        }
    }

    private static RegistrationStatusResponse getStatusResponse(Student student, Registration reg) {
        RegistrationStatusResponse registration = new RegistrationStatusResponse();
        registration.setStatus(COMPLETED);
        registration.setMessage("Your courses are already registered for the selected Academic year and Semester.");
        registration.setRegistered(true);
        registration.setSemesterName(reg.getSemester().getSemesterName());
        registration.setAcademicSession(reg.getSemester().getAcademicSession());
        registration.setSummary("Student has completed registration");
        return registration;
    }

    private static RegistrationStatusResponse getRegistrationStatusResponse(Student student) {
        RegistrationStatusResponse registration = new RegistrationStatusResponse();
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setMessage("Student has not yet registered");
        registration.setRegistered(false);
        registration.setAcademicSession(student.getAcademicSession());
        registration.setSummary("Student has not yet registered");
        return registration;
    }

    @Override
    public Response<List<CourseModel>> getRegisteredCourses() {
        String token = tokenService.getToken(servletRequest);
        TokenValidationResponse session = tokenService.validateToken(token);

        Student student = studentRepository.findByStudentId(session.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        Optional<Registration> reg = registrationRepository.findFirstByStudentsAndSemester_AcademicSessionOrderByIdDesc(student, student.getAcademicSession());
        if (reg.isEmpty()) {
            return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "No registration found", List.of());
        }

        List<String> courseCodes = reg.get().getCourses().stream()
                .map(Course::getCourseCode)
                .toList();
        List<CourseModel> courses = courseRepository.findByCourseCodeIn(courseCodes);
        return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "Registered courses retrieved successfully", courses);
    }

    /** Parses the starting year from a session string like "2024/2025" → 2024. */
    private static int parseSessionYear(String session) {
        if (session == null) return 0;
        try {
            return Integer.parseInt(session.split("/")[0].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    private void validateBusinessRules(Student student, List<Course> courses) {
        // Rule: Credit Units must be between 12 and 24
        int totalCredits = courses.stream().mapToInt(Course::getCreditUnit).sum();
        if (totalCredits < 12 || totalCredits > 24) {
            throw new NotFoundException("Total credits is either less than 12 or more than 24");
        }

        // Rule: Slot Availability
        for (Course course : courses) {
            if (course.getAvailableSlots() <= 0) {
                throw new NotFoundException("Course " + course.getCourseCode() + " has no remaining slots.");
            }
        }

    }
}
