package com.educonnect.qna.entity;


import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"}),
        @UniqueConstraint(columnNames = {"user_id", "answer_id"})
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = true)
    private Answer answer;

    private Integer value;

    private Instant createdAt;

    @PrePersist
    void beforeSaving(){
        this.createdAt = new Date().toInstant();
    }

}
