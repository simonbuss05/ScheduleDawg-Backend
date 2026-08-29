package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.GradeScaleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeScaleEntryRepository extends JpaRepository<GradeScaleEntry, Long> {
    List<GradeScaleEntry> findByCourseId(Long courseId);
}
