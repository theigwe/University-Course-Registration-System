package com.unilag.course_registration_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "faculties")
public class Faculty extends BaseEntity {
    private String facultyName;
    private String facultyCode;
    // One Faculty has many Departments
    @JsonIgnoreProperties("faculties")
    @OneToMany(mappedBy = "faculties", cascade = CascadeType.ALL)
    private List<Department> departments;

    public Faculty(String facultyName, String facultyCode) {
        this.facultyName = facultyName;
        this.facultyCode = facultyCode;
    }

}
