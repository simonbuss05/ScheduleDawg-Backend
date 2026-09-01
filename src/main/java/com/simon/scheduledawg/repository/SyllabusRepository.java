package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    // Explicit underscore notation (course_Id, not courseId) forces Spring
    // Data to treat this as "traverse the course relationship, then its id"
    // rather than trying to resolve a literal "courseId" attribute — which
    // it will find and fail to prefer, since Syllabus.getCourseId() (a
    // computed JSON-serialization convenience, not a persistent field) makes
    // "courseId" look like a real property name to the query parser.
    List<Syllabus> findByCourse_Id(Long courseId);
    List<Syllabus> findByCourse_User_Id(Long userId);
}

