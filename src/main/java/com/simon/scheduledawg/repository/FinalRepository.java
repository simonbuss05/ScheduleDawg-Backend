package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Final;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalRepository extends JpaRepository<Final, Long> {
    List<Final> findByCourseId(Long courseId);
}
