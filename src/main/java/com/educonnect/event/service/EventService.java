package com.educonnect.event.service;

import com.educonnect.event.dto.response.EventResponseDto;
import com.educonnect.event.dto.response.PagedResponse;
import com.educonnect.event.model.Events;
import com.educonnect.event.repo.EventsRepo;
import com.educonnect.event.repo.RegistrationRepo;
import com.educonnect.event.utility.EventMapper;
import com.educonnect.exceptionhandling.exception.EventNotFoundException;
import com.educonnect.user.entity.Users;
import com.educonnect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private EventsRepo erepo;

    @Autowired
    private RegistrationRepo repo;

    @Autowired
    private UserRepository uRepo;



//    @Cacheable(value = "events", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    public PagedResponse<EventResponseDto> getAllEvents(Pageable pageable) {
        log.info("Fetching all events with pagination: {}", pageable);
        Page<Events> eventsPage = erepo.findAll(pageable);
        List<EventResponseDto> eventDtos = eventsPage.getContent().stream()
                .map(EventMapper::toEventResponseDto)
                .toList();
        return PagedResponse.<EventResponseDto>builder()
                .content(eventDtos)
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .first(eventsPage.isFirst())
                .last(eventsPage.isLast())
                .build();
    }


    public Optional<Events> findEventById(Long eventId){
        return erepo.findById(eventId);
    }



    public Events addEvent(Events event , UUID userId) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Users creator = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        event.setCreatedBy(creator);


        validateEventData(event);


        return erepo.save(event);
    }

    private void validateEventData(Events event) {
        if(event.getTitle() == null || event.getTitle().trim().isEmpty()){
            throw new IllegalArgumentException("Event title cannot be empty");
        }

        if(event.getDate() == null){
            throw new IllegalArgumentException("Event date cannot be null");
        }
        if(event.getDate().before(new Date()))
        {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }

        if(event.getMaxParticipants() <= 0){
            throw new IllegalArgumentException("Maximum participants must be greater than 0");
        }
    }

    public Events updateEvent(Events newEvent , Long id , UUID userId) {
        Events crrEvent = erepo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + id)
        );

        if(!crrEvent.getCreatedBy().getId().equals(userId)){
            throw new IllegalArgumentException("You can only update events which created by you.");
        }

        crrEvent.setTitle(newEvent.getTitle());
        crrEvent.setDescription(newEvent.getDescription());
        crrEvent.setUniversity(newEvent.getUniversity());
        crrEvent.setDate(newEvent.getDate());
        crrEvent.setMaxParticipants(newEvent.getMaxParticipants());

        validateEventData(crrEvent);

        return erepo.save(crrEvent);
    }

    public void deleteEvent(Long id , UUID userId) {
        Events event = erepo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + id)
        );

        if(!event.getCreatedBy().getId().equals(userId)){
            throw new IllegalArgumentException("You can only update events which created by you.");
        }

        erepo.deleteById(id);
    }

    @Cacheable(value = "eventSearch", key = "#keyWord + '_' + #pageable.pageNumber + '_' + #pageable.pageSize ")
    public PagedResponse<EventResponseDto> searchEvents(String keyWord , Pageable pageable) {
        log.info("Searching events with keyword: {}", keyWord);

        if (!StringUtils.hasText(keyWord)) {
            return getAllEvents(pageable);
        }

//        if(keyWord == null || keyWord.trim().isEmpty()){
//            return getAllEvents();
//        }

        Page<Events> eventsPage = erepo.searchEvents(keyWord, pageable);
        List<EventResponseDto> eventdtoli = eventsPage.getContent().stream().map(EventMapper::toEventResponseDto).toList();

        return PagedResponse.<EventResponseDto>builder()
                .content(eventdtoli)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .first(eventsPage.isFirst())
                .last(eventsPage.isLast())
                .empty(eventsPage.isEmpty())
                .build();
    }

    public List<Events> getUpcomingEvents(){
        return erepo.findByDateAfterOrderByDateAsc(new Date());
    }

//    public List<Events> findEventByCreator(String username){
//        List<Users> user = uRepo.findByUsername(username);
////                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));;
//
//
//        return erepo.findByCreatedBy(user);
//    }

    public List<Events> getPastEvents(){
        return erepo.findByDateBeforeOrderByDateDesc(new Date());
    }

    public List<Events> getEventsByDateRange(Date startDate, Date endDate){
            return erepo.findByDateBetweenOrderByDateAsc(startDate, endDate);
    }


    public int getEventRegistrationCount(Long eventId){
        Events event = erepo.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + eventId)
        );

        return event.getCurrentParticipantCount();
    }

    public int getAvailableSpots(Long eventId){
        Events event = erepo.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + eventId)
        );

        return event.getMaxParticipants() - event.getCurrentParticipantCount();
    }

    public boolean isEventFull(Long eventId){
        Events event = erepo.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + eventId)
        );

        return event.isFull();
    }

    public boolean isEventActive(Long eventId){
        Events event = erepo.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + eventId)
        );
        return event.getDate().after(new Date());
    }
    
    
    
    public List<Events> getPopularEvents(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return erepo.findTopEventsByRegistrationCount(pageable);
    }

    public List<Events> getMyCreatedEvents(UUID userId){
        Users user = uRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return erepo.findByCreatedByOrderByCreatedAtDesc(user);
    }

    public long getTotalEventsCount(){
        return erepo.count();
    }

    public long getTotalActiveEventsCount() {
        return erepo.countByDateAfter(new Date());
    }

    public long getEventsByCreatorCount(UUID creatorId) {
        Users creator = uRepo.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorId));
        return erepo.countByCreatedBy(creator);
    }

    public boolean isUserEventCreator(Long eventId, UUID userId) {
        Events event = erepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        return event.getCreatedBy().getId().equals(userId);
    }

    public UUID getEventCreator(Long eventId) {
        Events event = erepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        return event.getCreatedBy().getId();
    }

}