package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Course;
import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final CourseService courseService;

    public MeetingService(MeetingRepository meetingRepository,  CourseService courseService) {
        this.meetingRepository = meetingRepository;
        this.courseService = courseService;
    }

    public Meeting createMeeting(Meeting meeting, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        meeting.setCourse(course);
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByCourse(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return meetingRepository.findByCourseId(courseId);
    }

    public Meeting getMeetingById(Long courseId, Long meetingId) {
        return getMeetingScopedToCourse(courseId, meetingId);
    }

    private Meeting getMeetingScopedToCourse(Long courseId, Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + meetingId));

        if (!meeting.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Meeting not found with id: " + meetingId);
        }

        return meeting;
    }

    public Meeting fullyUpdateMeeting(Meeting meeting, Long courseId, Long meetingId) {
        Meeting meetingToUpdate = getMeetingScopedToCourse(courseId, meetingId);
        meetingToUpdate.setDayOfWeek(meeting.getDayOfWeek());
        meetingToUpdate.setStartTime(meeting.getStartTime());
        meetingToUpdate.setEndTime(meeting.getEndTime());
        meetingToUpdate.setLocation(meeting.getLocation());
        return meetingRepository.save(meetingToUpdate);
    }

    public Meeting partialUpdateMeeting(Meeting meeting, Long courseId, Long meetingId) {
        Meeting meetingToUpdate = getMeetingScopedToCourse(courseId, meetingId);
        if (meeting.getDayOfWeek() != null) {
            meetingToUpdate.setDayOfWeek(meeting.getDayOfWeek());
        }
        if (meeting.getStartTime() != null) {
            meetingToUpdate.setStartTime(meeting.getStartTime());
        }
        if (meeting.getEndTime() != null) {
            meetingToUpdate.setEndTime(meeting.getEndTime());
        }
        if (meeting.getLocation() != null) {
            meetingToUpdate.setLocation(meeting.getLocation());
        }
        return meetingRepository.save(meetingToUpdate);
    }

    public void deleteSpecificMeeting(Long courseId, Long meetingId) {
        Meeting meetingToDelete = getMeetingScopedToCourse(courseId, meetingId);
        meetingRepository.delete(meetingToDelete);
    }

    public void deleteAllMeetingsByCourse(Long courseId) {
        List<Meeting> meetings = meetingRepository.findByCourseId(courseId);
        meetingRepository.deleteAll(meetings);
    }


}
