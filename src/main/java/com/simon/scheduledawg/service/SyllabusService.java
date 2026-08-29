package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.SyllabusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SyllabusService {

    private SyllabusRepository syllabusRepository;
    private CourseService courseService;

    public SyllabusService(SyllabusRepository syllabusRepository, CourseService courseService) {
        this.syllabusRepository = syllabusRepository;
        this.courseService = courseService;
    }

    public List<Syllabus> getSyllabiByCourse(Long courseId) {
        courseService.getCourseById(courseId); // verify course exists
        return syllabusRepository.findByCourseId(courseId);
    }

    public Syllabus createSyllabus(byte[] fileData, String fileName, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        Syllabus syllabus = new Syllabus();
        syllabus.setUploadedAt(LocalDateTime.now());
        syllabus.setCourse(course);
        syllabus.setFileName(fileName);
        syllabus.setFileData(fileData);
        return syllabusRepository.save(syllabus);
    }

    public List<Syllabus> getAllSyllabuses() {
        return syllabusRepository.findAll();
    }

    public Syllabus findSyllabusById(Long syllabusId, Long courseId) {
        return getSyllabusScopedToCourse(courseId, syllabusId);
    }

    private Syllabus getSyllabusScopedToCourse(Long courseId, Long syllabusId) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found with id: " + syllabusId));

        if (!syllabus.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Syllabus not found with id: " + syllabusId);
        }

        return syllabus;
    }

    public void deleteSyllabus(Long courseId, Long syllabusId) {
        Syllabus syllabus = getSyllabusScopedToCourse(courseId, syllabusId);
        syllabusRepository.delete(syllabus);
    }

    public void deleteAllSyllabusesByCourse(Long courseId) {
        List<Syllabus> syllabuses = syllabusRepository.findByCourseId(courseId);
        syllabusRepository.deleteAll(syllabuses);
    }



}
