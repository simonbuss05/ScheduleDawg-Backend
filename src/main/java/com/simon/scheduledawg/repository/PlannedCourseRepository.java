package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.PlannedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannedCourseRepository extends JpaRepository<PlannedCourse, Long> {
    List<PlannedCourse> findByUserId(Long userId);
}
