package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<Meeting>> getMeetings(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(meetingService.getMeetingsByCourse(courseId, currentUser));
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<Meeting> getMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(meetingService.getMeetingById(courseId, meetingId, currentUser));
    }

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@PathVariable Long courseId, @RequestBody Meeting meeting, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(meetingService.createMeeting(meeting, courseId, currentUser));
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<Meeting> fullyUpdateMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @RequestBody Meeting meeting, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(meetingService.fullyUpdateMeeting(meeting, courseId, meetingId, currentUser));
    }

    @PatchMapping("/{meetingId}")
    public ResponseEntity<Meeting> partialUpdateMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @RequestBody Meeting meeting, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(meetingService.partialUpdateMeeting(meeting, courseId, meetingId, currentUser));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMeetings(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        meetingService.deleteAllMeetingsByCourse(courseId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long courseId, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        meetingService.deleteSpecificMeeting(courseId, meetingId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
