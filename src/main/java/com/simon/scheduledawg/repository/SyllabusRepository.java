package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    List<Syllabus> findByCourseId(Long courseId);
    List<Syllabus> findByCourseUserId(Long userId);
}

