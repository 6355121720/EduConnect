package com.educonnect.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private String university;
    private Date date;
    private int maxParticipants;
    private int currentParticipants;
    private String createdByUsername;
    private UUID createdById;
    private  String createdByProfilePictureUrl;
}