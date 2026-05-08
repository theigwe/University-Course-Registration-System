package com.unilag.course_registration_system.model;

import org.springframework.beans.factory.annotation.Value;

public interface StudentModel {
    String getStudentId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber();
    String getAddress();
    String getCurrentLevel();
    @Value("#{target.departments.departmentName}")
    String getDepartmentName();
    @Value("#{target.departments.departmentCode}")
    String getDepartmentCode();
    @Value("#{target.departments.faculties.facultyName}")
    String getFacultyName();
    @Value("#{target.departments.faculties.facultyCode}")
    String getFacultyCode();
    String getAcademicSession();

}
