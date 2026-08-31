package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.ExternalSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalSyllabusRepository extends JpaRepository<ExternalSyllabus, Long> {
    Optional<ExternalSyllabus> findByExternalCourseInstructorId(Long externalCourseInstructorId);
}
