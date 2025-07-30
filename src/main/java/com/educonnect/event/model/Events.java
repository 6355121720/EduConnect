package com.educonnect.event.model;


import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity// Exclude relationships
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String university;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by" , nullable = false )
    private Users createdBy;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<Registration> registrations;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public int getCurrentParticipantCount() {
        return registrations != null ? registrations.size() : 0;
    }

    public boolean isFull() {
        return getCurrentParticipantCount() >= maxParticipants;
    }

    public String getEventName() {

        return title != null ? title : "No Title";
    }
}
