package com.unilag.course_registration_system.dto.request;

import com.unilag.course_registration_system.entity.Semester;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RegistrationRequest {
    private List<String> courseIds;
    private Semester semester;
}
