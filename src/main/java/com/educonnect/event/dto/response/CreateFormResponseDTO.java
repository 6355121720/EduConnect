package com.educonnect.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateFormResponseDTO {
    private Long id;
    private String title;
    @JsonProperty("isActive")
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FormFieldResponseDTO> fields;

    @Data
    public static class FormFieldResponseDTO {
        private Long id;
        private String label;
        private String type;
        private boolean required;
        private int orderIndex;
        private String placeholder;
        private String helpText;
        private String options;
    }
}
