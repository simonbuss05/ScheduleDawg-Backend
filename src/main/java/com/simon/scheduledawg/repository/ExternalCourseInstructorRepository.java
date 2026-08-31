package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.ExternalCourseInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalCourseInstructorRepository extends JpaRepository<ExternalCourseInstructor, Long> {
    List<ExternalCourseInstructor> findByExternalCourseId(Long externalCourseId);
}
