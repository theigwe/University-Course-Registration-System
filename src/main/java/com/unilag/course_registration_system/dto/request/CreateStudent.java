package com.unilag.course_registration_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudent {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String currentLevel;
    private Long departmentId;
    private String academicSession;
}
