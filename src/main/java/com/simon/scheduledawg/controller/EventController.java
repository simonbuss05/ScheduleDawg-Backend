package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Event;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings/{meetingId}/events")
public class EventController {

    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll(@PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.getEventsByMeeting(meetingId, currentUser));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> get(@PathVariable Long eventId, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.getEventById(meetingId, eventId, currentUser));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.createEvent(event, meetingId, currentUser));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody Event event, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.fullyUpdateEvent(event, meetingId, eventId, currentUser));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Event> patchEvent(@PathVariable Long eventId, @RequestBody Event event, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.partialUpdateEvent(event, meetingId, eventId, currentUser));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, @PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        eventService.deleteEvent(meetingId, eventId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllEvents(@PathVariable Long meetingId, @AuthenticationPrincipal User currentUser) {
        eventService.deleteAllEventsByMeeting(meetingId, currentUser);
        return ResponseEntity.noContent().build();
    }




}
