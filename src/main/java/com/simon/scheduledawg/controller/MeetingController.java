package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getMeetings(@PathVariable Long courseId) {
        return ResponseEntity.ok(meetingService.getMeetingsByCourse(courseId));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<Meeting> getMeeting(@PathVariable Long courseId, @PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMeetingById(courseId, meetingId));
    }

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@PathVariable Long courseId, @RequestBody Meeting meeting) {
        return ResponseEntity.ok(meetingService.createMeeting(meeting, courseId));
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<Meeting> fullyUpdateMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @RequestBody Meeting meeting) {
        return ResponseEntity.ok(meetingService.fullyUpdateMeeting(meeting, courseId, meetingId));
    }

    @PatchMapping("/{meetingId}")
    public ResponseEntity<Meeting> partialUpdateMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @RequestBody Meeting meeting) {
        return ResponseEntity.ok(meetingService.partialUpdateMeeting(meeting, courseId, meetingId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMeetings(@PathVariable Long courseId) {
        meetingService.deleteAllMeetingsByCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long courseId, @PathVariable Long meetingId) {
        meetingService.deleteSpecificMeeting(courseId, meetingId);
        return ResponseEntity.noContent().build();
    }
}