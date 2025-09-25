package com.educonnect.event.repo;

import com.educonnect.event.model.Ticket;
import com.educonnect.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xhtmlrenderer.pdf.PagePosition;


public interface TickerRepo extends JpaRepository<Ticket , Long> {
    Ticket findByRegistrationId(Long registrationId);

    Ticket findByRegistrationIdAndUser(Long registrationId, Users user);
}
