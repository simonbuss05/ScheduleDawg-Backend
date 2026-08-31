package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.CourseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SemesterService semesterService;

    public CourseService(CourseRepository courseRepository, SemesterService semesterService) {
        this.courseRepository = courseRepository;
        this.semesterService = semesterService;
    }

    public static void verifyOwnership(Course course, User currentUser) {
        if (course.getUser() == null || !course.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have access to that course.");
        }
    }

    public List<Course> getAllCourses(Long semesterId, User currentUser){
        Long targetSemesterId = semesterId != null
                ? semesterId
                : semesterService.getActiveSemester(currentUser).getId();
        return courseRepository.findByUserIdAndSemesterId(currentUser.getId(), targetSemesterId);
    }

    public Course getCourseById(Long id, User currentUser) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        verifyOwnership(course, currentUser);
        return course;
    }

    public Course createCourse(Course course, User currentUser){
        course.setUser(currentUser);
        course.setSemester(semesterService.getActiveSemester(currentUser));
        return courseRepository.save(course);
    }

    public Course updateFullCourse(Long id, Course course, User currentUser){
        Course courseToUpdate = getCourseById(id, currentUser);
        courseToUpdate.setName(course.getName());
        courseToUpdate.setCode(course.getCode());
        courseToUpdate.setProfessor(course.getProfessor());
        courseToUpdate.setCreditHours(course.getCreditHours());
        return courseRepository.save(courseToUpdate);
    }

    public void deleteCourse(Long id, User currentUser){
        Course course = getCourseById(id, currentUser);
        courseRepository.delete(course);
    }

    public void deleteAllCourses(User currentUser){
        courseRepository.deleteByUserId(currentUser.getId());
    }

    public Course updatePartialCourse(Long id, Course course, User currentUser){
       Course courseToUpdate = getCourseById(id, currentUser);

       if (course.getName() != null) {
           courseToUpdate.setName(course.getName());
       }
       if (course.getCode() != null) {
           courseToUpdate.setCode(course.getCode());
       }
       if (course.getProfessor() != null) {
           courseToUpdate.setProfessor(course.getProfessor());
       }
       if (course.getCreditHours() != null) {
           courseToUpdate.setCreditHours(course.getCreditHours());
       }
       return courseRepository.save(courseToUpdate);
    }



}
