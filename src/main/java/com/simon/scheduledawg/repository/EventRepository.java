package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCourseId(Long courseId);
}
