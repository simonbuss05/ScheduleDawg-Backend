package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Event;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CourseService courseService;

    public EventService(EventRepository eventRepository, CourseService courseService) {
        this.eventRepository = eventRepository;
        this.courseService = courseService;
    }

    public Event createEvent(Event event, Long courseId, User currentUser){
        event.setCourse(courseService.getCourseById(courseId, currentUser));
        return eventRepository.save(event);
    }

    public List<Event> getEventsByCourseId(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        return eventRepository.findByCourseId(courseId);
    }

    public Event getEventById(Long courseId, Long eventId, User currentUser) {
        return getEventScopedToCourse(courseId, eventId, currentUser);
    }

    public Event fullyUpdateEvent(Event event, Long courseId, Long eventId, User currentUser){
        Event eventToUpdate = getEventScopedToCourse(courseId, eventId, currentUser);
        eventToUpdate.setTitle(event.getTitle());
        eventToUpdate.setDescription(event.getDescription());
        eventToUpdate.setEventDate(event.getEventDate());
        return eventRepository.save(eventToUpdate);
    }

    public Event partialUpdateEvent(Event event, Long courseId, Long eventId, User currentUser){
        Event eventToUpdate = getEventScopedToCourse(courseId, eventId, currentUser);

        if (event.getTitle() != null) {
            eventToUpdate.setTitle(event.getTitle());
        }
        if (event.getDescription() != null) {
            eventToUpdate.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            eventToUpdate.setEventDate(event.getEventDate());
        }

        return eventRepository.save(eventToUpdate);
    }

    public void deleteEvent(Long courseId, Long eventId, User currentUser){
        Event event = getEventScopedToCourse(courseId, eventId, currentUser);
        eventRepository.delete(event);
    }

    private Event getEventScopedToCourse(Long courseId, Long eventId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (!event.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        return event;
    }

    public void deleteAllEventsByCourseId(Long courseId, User currentUser) {
        courseService.getCourseById(courseId, currentUser);
        eventRepository.deleteAll(eventRepository.findByCourseId(courseId));
    }
}
