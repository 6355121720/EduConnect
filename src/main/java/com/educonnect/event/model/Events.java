package com.educonnect.event.model;

import com.educonnect.event.enums.RegistrationStatus;
import com.educonnect.user.entity.Users;
import com.educonnect.event.enums.EventStatus;
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
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String location; // Physical address or online link
    private String bannerUrl; // Optional banner image

    private String attachmentUrl; // Optional attachment file

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;

    private String university;

    @Column(nullable = false)
    private Long maxParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventRole> eventRoles;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegistrationForm> registrationForms;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PostUpdate
    protected void afterUpdate() {
        if (EventStatus.PUBLISHED.equals(this.status) && registrations != null) {
            for (Registration reg : registrations) {
                if(reg.getTicket() != null){
                    reg.getTicket().syncWithEvent();
                }
            }
        }
    }
    // Business logic methods
    public long getCurrentParticipantCount() {
        return registrations != null ? registrations.size() : 0;
    }

    public boolean isFull() {
        return getCurrentParticipantCount() >= maxParticipants;
    }

    public String getEventName() {
        return title != null ? title : "No Title";
    }

    public boolean isPublished() {
        return EventStatus.PUBLISHED.equals(this.status);
    }

    public boolean isCancelled() {
        return EventStatus.CANCELLED.equals(this.status);
    }
}