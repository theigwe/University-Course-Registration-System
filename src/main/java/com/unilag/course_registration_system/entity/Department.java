package com.unilag.course_registration_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
@Table(name = "departments")
@Entity
public class Department extends BaseEntity {
    private String departmentName;
    private String departmentCode;

    @JsonIgnoreProperties("departments")
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculties;

    @JsonIgnoreProperties("departments")
    @OneToMany(mappedBy = "departments")
    private List<Student> students;

    public Department(String departmentName, String departmentCode, Faculty faculties) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.faculties = faculties;
    }

}
