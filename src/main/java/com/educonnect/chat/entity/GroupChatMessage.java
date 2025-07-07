package com.educonnect.chat.entity;


import com.educonnect.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.WithBy;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GroupChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String fileUrl;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupChat groupChat;

    public enum MediaType{
        TEXT,
        FILE
    }


}
