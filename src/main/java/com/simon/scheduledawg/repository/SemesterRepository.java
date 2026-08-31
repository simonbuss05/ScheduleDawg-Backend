package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByUserIdOrderByIdDesc(Long userId);
    Optional<Semester> findByUserIdAndActiveTrue(Long userId);
}
