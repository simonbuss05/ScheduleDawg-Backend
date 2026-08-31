package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.Syllabus;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.SyllabusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Syllabus> getSyllabiByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser); // verify ownership
        return syllabusRepository.findByCourseId(courseId);
    }

    public Syllabus createSyllabus(byte[] fileData, String fileName, Long courseId, User currentUser) {
        Course course = courseService.getCourseById(courseId, currentUser);
        Syllabus syllabus = new Syllabus();
        syllabus.setUploadedAt(LocalDateTime.now());
        syllabus.setCourse(course);
        syllabus.setFileName(fileName);
        syllabus.setFileData(fileData);
        return syllabusRepository.save(syllabus);
    }

    @Transactional(readOnly = true)
    public List<Syllabus> getAllSyllabuses(User currentUser) {
        return syllabusRepository.findByCourseUserId(currentUser.getId());
    }

    public Syllabus findSyllabusById(Long courseId, Long syllabusId, User currentUser) {
        return getSyllabusScopedToCourse(courseId, syllabusId, currentUser);
    }

    private Syllabus getSyllabusScopedToCourse(Long courseId, Long syllabusId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found with id: " + syllabusId));

        if (!syllabus.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Syllabus not found with id: " + syllabusId);
        }

        return syllabus;
    }

    public void deleteSyllabus(Long courseId, Long syllabusId, User currentUser) {
        Syllabus syllabus = getSyllabusScopedToCourse(courseId, syllabusId, currentUser);
        syllabusRepository.delete(syllabus);
    }

    public void deleteAllSyllabusesByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        List<Syllabus> syllabuses = syllabusRepository.findByCourseId(courseId);
        syllabusRepository.deleteAll(syllabuses);
    }

    public Syllabus getSyllabusById(Long syllabusId, User currentUser) {
        Syllabus syllabus = syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found with id: " + syllabusId));
        CourseService.verifyOwnership(syllabus.getCourse(), currentUser);
        return syllabus;
    }

    public void deleteSyllabus(Long syllabusId, User currentUser) {
        Syllabus syllabus = getSyllabusById(syllabusId, currentUser);
        syllabusRepository.delete(syllabus);
    }



}
