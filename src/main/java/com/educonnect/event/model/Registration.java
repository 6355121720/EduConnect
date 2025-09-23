package com.educonnect.event.model;

import com.educonnect.event.enums.RegistrationStatus;
import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Table(name = "registrations" ,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id" , "user_id"})
        })
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable = false)
    private Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private Users user;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "form_response_id", unique = true)
    private FormResponse formResponse;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_form_id")
    private RegistrationForm registrationForm;

    @Column(nullable = false)
    private Boolean requiresFormSubmission = true;

    @Column(nullable = false)
    private Boolean formSubmitted = false;


    private LocalDateTime statusUpdatedAt;


    public Registration(Events event, Users user) {
        this.event = event;
        this.user = user;
        this.registrationDate = LocalDateTime.now();
    }

    public Registration(Events event, Users user, RegistrationForm registrationForm) {
        this.event = event;
        this.user = user;
        this.registrationForm = registrationForm;
        this.registrationDate = LocalDateTime.now();
        this.requiresFormSubmission = (registrationForm != null);
    }

    @PrePersist
    protected void onCreate() {
        if (this.registrationDate == null) {
            this.registrationDate = LocalDateTime.now();
        }
        this.statusUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.statusUpdatedAt = LocalDateTime.now();
    }

    public boolean canBeConfirmed() {
        return !requiresFormSubmission || formSubmitted;
    }



    public void markFormAsSubmitted(FormResponse formResponse) {
        this.formResponse = formResponse;
        this.formSubmitted = true;
    }

}