package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.CreateFaculty;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.entity.Faculty;
import com.unilag.course_registration_system.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("faculty/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FacultyController {
    private final FacultyService facultyService;

    @PostMapping("create")
    public Response<Void> createFaculty(@RequestBody CreateFaculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("fetch")
    public Response<List<Faculty>> fetchFaculty() {
        return facultyService.fetchAllFaculties();
    }
}
