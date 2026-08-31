package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.Event;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/events")
public class EventController {

    private final EventService eventService;
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.getEventsByCourseId(courseId, currentUser));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> get(@PathVariable Long eventId, @PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.getEventById(courseId, eventId, currentUser));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event, @PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.createEvent(event, courseId, currentUser));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody Event event, @PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.fullyUpdateEvent(event, courseId, eventId, currentUser));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Event> patchEvent(@PathVariable Long eventId, @RequestBody Event event, @PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(eventService.partialUpdateEvent(event, courseId, eventId, currentUser));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, @PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        eventService.deleteEvent(courseId, eventId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllEvents(@PathVariable Long courseId, @AuthenticationPrincipal User currentUser) {
        eventService.deleteAllEventsByCourseId(courseId, currentUser);
        return ResponseEntity.noContent().build();
    }

}
