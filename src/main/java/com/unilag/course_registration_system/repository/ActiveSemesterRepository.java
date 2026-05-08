package com.unilag.course_registration_system.repository;

import com.unilag.course_registration_system.entity.ActiveSemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ActiveSemesterRepository extends JpaRepository<ActiveSemester, Long> {
    Optional<ActiveSemester> findByActiveTrue();

    @Modifying
    @Query("UPDATE ActiveSemester a SET a.active = false")
    void deactivateAll();
}
