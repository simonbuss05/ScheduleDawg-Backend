package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course createCourse(Course course){
        return courseRepository.save(course);
    }

    public Course updateFullCourse(Long id, Course course){
        Course courseToUpdate = getCourseById(id);
        courseToUpdate.setName(course.getName());
        courseToUpdate.setCode(course.getCode());
        courseToUpdate.setProfessor(course.getProfessor());
        courseToUpdate.setCreditHours(course.getCreditHours());
        return courseRepository.save(courseToUpdate);
    }

    public void deleteCourse(Long id){
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public void deleteAllCourses(){
        courseRepository.deleteAll();
    }

    public Course updatePartialCourse(Long id, Course course){
       Course courseToUpdate = getCourseById(id);

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
