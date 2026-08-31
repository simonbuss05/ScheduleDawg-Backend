package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.ExternalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalCourseRepository extends JpaRepository<ExternalCourse, Long> {
    Optional<ExternalCourse> findBySubjectCodeIgnoreCaseAndCourseNumberIgnoreCase(String subjectCode, String courseNumber);
}
