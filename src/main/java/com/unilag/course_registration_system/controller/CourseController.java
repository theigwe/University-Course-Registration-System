package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.CreateCourse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.model.CourseModel;
import com.unilag.course_registration_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public Response<Void> createCourse(@RequestBody CreateCourse request) {
        return courseService.createCourse(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Response<List<CourseModel>> fetchCourse() {
        return courseService.fetchCourses();
    }

}
