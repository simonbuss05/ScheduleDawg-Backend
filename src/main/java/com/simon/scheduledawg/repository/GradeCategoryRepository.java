package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.GradeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeCategoryRepository extends JpaRepository<GradeCategory, Long> {
    List<GradeCategory> findByCourseId(Long courseId);
}
