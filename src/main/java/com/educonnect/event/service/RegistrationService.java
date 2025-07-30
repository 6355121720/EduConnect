package com.educonnect.event.service;


import com.educonnect.event.dto.response.RegistrationDTO;
import com.educonnect.event.model.Events;
import com.educonnect.event.model.Registration;
import com.educonnect.event.model.Ticket;
import com.educonnect.event.repo.EventsRepo;
import com.educonnect.event.repo.RegistrationRepo;
import com.educonnect.event.repo.TickerRepo;
import com.educonnect.exceptionhandling.exception.EventNotFoundException;
import com.educonnect.user.entity.Users;
import com.educonnect.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RegistrationService {

    @Autowired
    private RegistrationRepo rRepo;
    @Autowired
    private EventsRepo eRepo;

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private TickerRepo trepo;

    public Registration userRegister(Long eventId , UUID userId) {
        Events event = eRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        Users user = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        if(event.isFull()){
            throw new RuntimeException("Event is full");
        }

        if(rRepo.existsByEventAndUser(event , user)){
            throw new RuntimeException("User already registered for this event");
        }

        Registration registration = new Registration(event , user);

        Ticket ticket = new Ticket(true ,  event , user, registration);
        rRepo.save(registration);
        trepo.save(ticket);
        return registration;
    }

    public void removeRegistration(Long eventId , UUID userId){
        Events event = eRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        Users user = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        Registration registration = rRepo.findByEventAndUser(event, user)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        rRepo.delete(registration);
    }

    public List<RegistrationDTO> getMyRegistration(UUID userId){
        Users user = uRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
        List<Registration> registrations = rRepo.findByUser(user);
        return registrations.stream()
                .map(RegistrationDTO::from)
                .toList();
//        return rRepo.findByUser(user);
    }

    public List<RegistrationDTO> getEventRegistration(Long eventId){
        Events event = eRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event Not Found"));
        List<Registration> registrations = rRepo.findByEvent(event);
        return registrations.stream().map(RegistrationDTO::from).toList();
    }

}