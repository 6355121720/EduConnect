package com.educonnect.event.repo;

import com.educonnect.event.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TickerRepo extends JpaRepository<Ticket , Long> {
    Ticket findByRegistrationId(Long registrationId);
}
