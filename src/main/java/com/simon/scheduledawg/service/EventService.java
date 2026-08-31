package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Event;
import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.EventRepository;
import com.simon.scheduledawg.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MeetingRepository meetingRepository;

    public EventService(EventRepository eventRepository, MeetingRepository meetingRepository) {
        this.eventRepository = eventRepository;
        this.meetingRepository = meetingRepository;
    }

    private Meeting getOwnedMeeting(Long meetingId, User currentUser) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + meetingId));
        CourseService.verifyOwnership(meeting.getCourse(), currentUser);
        return meeting;
    }

    public Event createEvent(Event event, Long meetingId, User currentUser){
        Meeting meeting = getOwnedMeeting(meetingId, currentUser);
        event.setMeeting(meeting);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByMeeting(Long meetingId, User currentUser) {
        getOwnedMeeting(meetingId, currentUser);
        return eventRepository.findByMeetingId(meetingId);
    }

    public Event getEventById(Long meetingId, Long eventId, User currentUser) {
        return getEventScopedToMeeting(meetingId, eventId, currentUser);
    }

    public Event fullyUpdateEvent(Event event, Long meetingId, Long eventId, User currentUser){
        Event eventToUpdate = getEventScopedToMeeting(meetingId, eventId, currentUser);
        eventToUpdate.setTitle(event.getTitle());
        eventToUpdate.setDescription(event.getDescription());
        eventToUpdate.setEventDate(event.getEventDate());
        return eventRepository.save(eventToUpdate);
    }

    public Event partialUpdateEvent(Event event, Long meetingId, Long eventId, User currentUser){
        Event eventToUpdate = getEventScopedToMeeting(meetingId, eventId, currentUser);

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

    public void deleteEvent(Long meetingId, Long eventId, User currentUser){
        Event event = getEventScopedToMeeting(meetingId, eventId, currentUser);
        eventRepository.delete(event);
    }

    private Event getEventScopedToMeeting(Long meetingId, Long eventId, User currentUser) {
        getOwnedMeeting(meetingId, currentUser);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (!event.getMeeting().getId().equals(meetingId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        return event;
    }

    public void deleteAllEventsByMeeting(Long meetingId, User currentUser) {
        getOwnedMeeting(meetingId, currentUser);
        eventRepository.deleteAll(eventRepository.findByMeetingId(meetingId));
    }
}
