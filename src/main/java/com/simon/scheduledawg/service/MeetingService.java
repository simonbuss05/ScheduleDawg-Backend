package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.entity.User;
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

    public Meeting createMeeting(Meeting meeting, Long courseId, User currentUser) {
        meeting.setCourse(courseService.getCourseById(courseId, currentUser));
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        return meetingRepository.findByCourseId(courseId);
    }

    public Meeting getMeetingById(Long courseId, Long meetingId, User currentUser) {
        return getMeetingScopedToCourse(courseId, meetingId, currentUser);
    }

    private Meeting getMeetingScopedToCourse(Long courseId, Long meetingId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + meetingId));

        if (!meeting.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Meeting not found with id: " + meetingId);
        }

        return meeting;
    }

    public Meeting fullyUpdateMeeting(Meeting meeting, Long courseId, Long meetingId, User currentUser) {
        Meeting meetingToUpdate = getMeetingScopedToCourse(courseId, meetingId, currentUser);
        meetingToUpdate.setDayOfWeek(meeting.getDayOfWeek());
        meetingToUpdate.setStartTime(meeting.getStartTime());
        meetingToUpdate.setEndTime(meeting.getEndTime());
        meetingToUpdate.setRoomNumber(meeting.getRoomNumber());
        meetingToUpdate.setBuilding(meeting.getBuilding());
        return meetingRepository.save(meetingToUpdate);
    }

    public Meeting partialUpdateMeeting(Meeting meeting, Long courseId, Long meetingId, User currentUser) {
        Meeting meetingToUpdate = getMeetingScopedToCourse(courseId, meetingId, currentUser);
        if (meeting.getDayOfWeek() != null) {
            meetingToUpdate.setDayOfWeek(meeting.getDayOfWeek());
        }
        if (meeting.getStartTime() != null) {
            meetingToUpdate.setStartTime(meeting.getStartTime());
        }
        if (meeting.getEndTime() != null) {
            meetingToUpdate.setEndTime(meeting.getEndTime());
        }
        if (meeting.getBuilding() != null) {
            meetingToUpdate.setBuilding(meeting.getBuilding());
        }
        if (meeting.getRoomNumber() != null) {
            meetingToUpdate.setRoomNumber(meeting.getRoomNumber());
        }
        return meetingRepository.save(meetingToUpdate);
    }

    public void deleteSpecificMeeting(Long courseId, Long meetingId, User currentUser) {
        Meeting meetingToDelete = getMeetingScopedToCourse(courseId, meetingId, currentUser);
        meetingRepository.delete(meetingToDelete);
    }

    public void deleteAllMeetingsByCourse(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        List<Meeting> meetings = meetingRepository.findByCourseId(courseId);
        meetingRepository.deleteAll(meetings);
    }


}
