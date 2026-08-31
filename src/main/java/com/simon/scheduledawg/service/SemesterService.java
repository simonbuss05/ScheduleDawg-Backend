package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Semester;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.SemesterRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public List<Semester> getSemesters(User currentUser) {
        return semesterRepository.findByUserIdOrderByIdDesc(currentUser.getId());
    }

    /**
     * Returns the user's active semester, creating a default one (named after
     * the current season/year) if they somehow don't have one yet — every
     * account gets one automatically at registration, but this keeps course
     * creation from ever hard-failing on a missing semester.
     */
    @Transactional
    public Semester getActiveSemester(User currentUser) {
        return semesterRepository.findByUserIdAndActiveTrue(currentUser.getId())
                .orElseGet(() -> createSemester(defaultSemesterName(), currentUser));
    }

    @Transactional
    public Semester createSemester(String name, User currentUser) {
        semesterRepository.findByUserIdAndActiveTrue(currentUser.getId()).ifPresent(current -> {
            current.setActive(false);
            semesterRepository.save(current);
        });

        Semester semester = new Semester(name, currentUser);
        return semesterRepository.save(semester);
    }

    @Transactional
    public Semester activateSemester(Long semesterId, User currentUser) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with id: " + semesterId));

        if (semester.getUser() == null || !semester.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have access to that semester.");
        }

        semesterRepository.findByUserIdAndActiveTrue(currentUser.getId()).ifPresent(current -> {
            current.setActive(false);
            semesterRepository.save(current);
        });

        semester.setActive(true);
        return semesterRepository.save(semester);
    }

    public static String defaultSemesterName() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        String season = month <= 4 ? "Spring" : month <= 7 ? "Summer" : "Fall";
        return season + " " + now.getYear();
    }
}
