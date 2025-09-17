package com.educonnect.event.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateFormRequestDTO {
    private String title;
    private boolean isActive;
    private List<CreateFormFieldDTO> fields;

    @Data
    public static class CreateFormFieldDTO {
        private String label;
        private String type;
        private boolean required;
        private int orderIndex;
        private String placeholder;
        private String helpText;
        private String options;
    }
}
