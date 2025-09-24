package com.educonnect.event.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateFormRequestDTO {
    private String title;
    private List<UpdateFormFieldDTO> fields;
    private LocalDateTime deadline;

    @Data
    public static class UpdateFormFieldDTO {
        private Long id; // For existing fields, null for new fields
        private String label;
        private String type;
        private boolean required;
        private int orderIndex;
        private String placeholder;
        private String helpText;
        private String options;
        private boolean isDeleted; // Flag to indicate if the field should be deleted
    }
}