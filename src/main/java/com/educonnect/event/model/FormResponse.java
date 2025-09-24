package com.educonnect.event.model;


import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "form_responses" ,
        uniqueConstraints = @UniqueConstraint(columnNames = {"form_id" , "participant_id"}))
public class FormResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private RegistrationForm form;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Users participant;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FormFieldResponse> fieldResponses;

    @OneToOne(mappedBy = "formResponse", fetch = FetchType.LAZY)
    private Registration registration;


    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean hasRegistration() {
        return registration != null;
    }

    public Long getEventId() {
        return form != null && form.getEvent() != null ? form.getEvent().getId() : null;
    }


}