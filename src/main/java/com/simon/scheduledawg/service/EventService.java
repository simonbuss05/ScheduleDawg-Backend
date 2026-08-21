package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.Event;
import com.simon.scheduledawg.entity.Meeting;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.EventRepository;
import com.simon.scheduledawg.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingService meetingService;

    public EventService(EventRepository eventRepository, MeetingRepository meetingRepository, MeetingService meetingService) {
        this.eventRepository = eventRepository;
        this.meetingRepository = meetingRepository;
        this.meetingService = meetingService;
    }

    public Event createEvent(Event event, Long meetingId){
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + meetingId));
        event.setMeeting(meeting);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByMeeting(Long meetingId) {
        meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + meetingId));
        return eventRepository.findByMeetingId(meetingId);
    }

    public Event getEventById(Long meetingId, Long eventId) {
        return getEventScopedToMeeting(meetingId, eventId);
    }

    public Event fullyUpdateEvent(Event event, Long meetingId, Long eventId){
        Event eventToUpdate = getEventScopedToMeeting(meetingId, eventId);
        eventToUpdate.setTitle(event.getTitle());
        eventToUpdate.setDescription(event.getDescription());
        eventToUpdate.setEventDate(event.getEventDate());
        return eventRepository.save(eventToUpdate);
    }

    public Event partialUpdateEvent(Event event, Long meetingId, Long eventId){
        Event eventToUpdate = getEventScopedToMeeting(meetingId, eventId);

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

    public void deleteEvent(Long meetingId, Long eventId){
        Event event = getEventById(meetingId, eventId);
        eventRepository.delete(event);
    }

    private Event getEventScopedToMeeting(Long meetingId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (!event.getMeeting().getId().equals(meetingId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }

        return event;
    }

    public void deleteAllEventsByMeeting(Long meetingId) {
        eventRepository.deleteAll(eventRepository.findByMeetingId(meetingId));
    }
}