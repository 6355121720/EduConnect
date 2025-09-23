package com.educonnect.event.dto.response;
import com.educonnect.event.model.Registration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventDescription;
    private String university;
    private LocalDateTime eventDate;
    private Long maxParticipants;
    private Long currentParticipants;
    private UUID userId;
    private String userFullName;
//    private String userLastName;
    private String userEmail;
    private LocalDateTime registrationDate;

    // Constructor to create DTO from Registration entity
    public RegistrationDTO(Registration registration) {
        this.id = registration.getId();
        this.eventId = registration.getEvent().getId();
        this.eventTitle = registration.getEvent().getTitle();
        this.eventDescription = registration.getEvent().getDescription();
        this.university = registration.getEvent().getUniversity();
        this.eventDate = registration.getEvent().getStartDate();
        this.maxParticipants = registration.getEvent().getMaxParticipants();
        this.currentParticipants = registration.getEvent().getCurrentParticipantCount();
        this.userId = registration.getUser().getId();
        this.userFullName = registration.getUser().getFullName();
//        this.userLastName = registration.getUser().getLastName();
        this.userEmail = registration.getUser().getEmail();
        this.registrationDate = registration.getRegistrationDate();
    }

    public static RegistrationDTO from(Registration registration) {
        return new RegistrationDTO(registration);
    }
}