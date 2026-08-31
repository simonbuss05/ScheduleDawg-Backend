package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserId(Long userId);
    List<Course> findByUserIdAndSemesterId(Long userId, Long semesterId);
    void deleteByUserId(Long userId);
}
