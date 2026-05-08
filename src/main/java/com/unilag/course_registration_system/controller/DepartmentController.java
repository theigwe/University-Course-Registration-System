package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.CreateDepartment;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.entity.Department;
import com.unilag.course_registration_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("department")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("create")
    public Response<Void> createDepartment(@RequestBody CreateDepartment request) {
        return departmentService.createDepartment(request);
    }

    @GetMapping("fetch")
    public Response<List<Department>> fetchDepartments() {
        return departmentService.fetchDepartments();
    }
}
