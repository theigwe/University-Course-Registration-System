package com.unilag.course_registration_system;

import com.unilag.course_registration_system.entity.Course;
import com.unilag.course_registration_system.entity.Department;
import com.unilag.course_registration_system.entity.Faculty;
import com.unilag.course_registration_system.entity.Semester;
import com.unilag.course_registration_system.entity.Student;
import com.unilag.course_registration_system.repository.CourseRepository;
import com.unilag.course_registration_system.repository.DepartmentRepository;
import com.unilag.course_registration_system.repository.FacultyRepository;
import com.unilag.course_registration_system.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CourseRegistrationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseRegistrationSystemApplication.class, args);
	}
    /**
     * Running this for the first time?
     * Uncomment Runner
     * Purpose:
     * To seed a student profile
     *
     */



//    @Bean
//    CommandLineRunner runner(
//            FacultyRepository facultyRepo,
//            DepartmentRepository deptRepo,
//            CourseRepository courseRepo,
//            StudentRepository studentRepo) {

//        return args -> {
//            // 1. Seed Faculty
//            Faculty science = new Faculty();
//            science.setFacultyCode("SCI");
//            science.setFacultyName("Faculty of Science");
//            facultyRepo.save(science);

//            // 2. Seed Department
//            Department cscDept = new Department();
//            cscDept.setDepartmentCode("CSC");
//            cscDept.setDepartmentName("Computer Science");
//            cscDept.setFaculties(science);
//            deptRepo.save(cscDept);

//            // 3. Seed Courses (Aligning with 12-24 credit unit rule)
//            Course c1 = new Course("CSC301", "Java Programming", 4, null,100, "200L", cscDept);
//            Course c2 = new Course("CSC302", "Database Systems", 4, null,50, "200L", cscDept);
//            Course c3 = new Course("CSC303", "Data Structures", 4, null,130, "200L", cscDept);
//            Course c4 = new Course("CSC304", "Software Engineering", 3, null,120, "200L", cscDept);
//            Course c5 = new Course("MTH301", "Calculus III", 3, null,150, "200L", cscDept);

//            courseRepo.saveAll(List.of(c1, c2, c3, c4, c5));

//            // 4. Create a Test Student (Matching the YAML example ID)
//            Student student = new Student();
//            student.setStudentId("STU-2025-001");
//            student.setFirstName("John");
//            student.setLastName("Doe");
//            student.setEmail("john.doe@university.edu");
//            student.setCurrentLevel("200L");
//            student.setAddress("Lagos Nigeria");
//            student.setDepartments(cscDept);


//            // Set current enrolled semester
//            Semester currentSemester = new Semester();
//            currentSemester.setSemesterName("First Semester");
//            currentSemester.setAcademicSession( "2025/2026");
//            student.setSemester(currentSemester);


//            studentRepo.save(student);
//            System.out.println("Student Id "+ student.getStudentId());

//            System.out.println("✅ Database Seeded successfully with Faculty, Dept, Courses and Student: STU-2025-001");
//        };
//    }

}
