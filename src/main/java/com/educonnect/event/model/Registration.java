package com.educonnect.event.model;

import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "registrations" ,
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"event_id" , "user_id"})
        })
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable = false)
    private Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private Users user;

    @Column(nullable = false)
    private Date registrationDate;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ticket ticket;

    public Registration(Events event, Users user) {
        this.event = event;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.registrationDate = new Date();
    }


}
