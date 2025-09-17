package com.educonnect.event.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class UpdateFormRequestDTO {
    private String title;
    private List<UpdateFormFieldDTO> fields;

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
    }
}