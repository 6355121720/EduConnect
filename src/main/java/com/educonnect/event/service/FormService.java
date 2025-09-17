
package com.educonnect.event.service;

import com.educonnect.event.dto.request.CreateFormRequestDTO;
import com.educonnect.event.dto.request.UpdateFormRequestDTO;
import com.educonnect.event.dto.response.CreateFormResponseDTO;
import com.educonnect.event.enums.EventRoleType;
import com.educonnect.event.enums.FieldType;
import com.educonnect.event.model.*;
import com.educonnect.event.repo.EventsRepo;
import com.educonnect.event.repo.RegistrationFormRepo;
import com.educonnect.event.utility.RegistrationFormMapper;
import com.educonnect.exceptionhandling.exception.EventNotFoundException;
import com.educonnect.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FormService {

    private final EventsRepo eventsRepo;
    private final RegistrationFormRepo registrationFormRepo;
    private final EventService eventService;
    private final RegistrationFormMapper registrationFormMapper;
    private final EventRoleService eventRoleService;

    public CreateFormResponseDTO createForm(Long eventId, CreateFormRequestDTO formRequestDTO, Users currentUser) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        if(!eventService.isUserEventCreator(eventId, currentUser.getId())) {
            throw new IllegalArgumentException("Only event creator can create forms");
        }

        if (hasActiveForm(event)) {
            throw new IllegalArgumentException("An active form already exists for this event");
        }



        RegistrationForm form = registrationFormMapper.toEntity(formRequestDTO);
        form.setEvent(event);


        if(form.getFields() != null && !form.getFields().isEmpty()) {
            setupFieldRelationships(form);
            validateFields(form.getFields());
        }

        RegistrationForm savedForm = registrationFormRepo.save(form);
        log.info("Form {} created for event {} by user {} with status: {}",
                savedForm.getId(), eventId, currentUser.getId(),
                savedForm.getIsActive() ? "active" : "inactive");

        return registrationFormMapper.toResponseDTO(savedForm);
    }


    private boolean hasActiveForm(Events event) {
        return registrationFormRepo.existsByEventAndIsActiveTrue(event);
    }

    private void validateFields(List<FormField> fields) {
        for(FormField f : fields){
            if(f.getLabel() == null || f.getLabel().trim().isEmpty()){
                throw new IllegalArgumentException("Field label cannot be empty");
            }
            if(f.getType() == null){
                throw new IllegalArgumentException("Field type cannot be null");
            }
            if(f.getRequired() == null){
                f.setRequired(false);
            }
        }
    }

    private void setupFieldRelationships(RegistrationForm form) {
        for(int i = 0 ; i < form.getFields().size() ; i++) {
            FormField field = form.getFields().get(i);
            field.setForm(form);

            if (field.getOrderIndex() == null) {
                field.setOrderIndex(i);
            }

            if (field.getFieldResponses() != null && !field.getFieldResponses().isEmpty()) {
                for (FormFieldResponse r : field.getFieldResponses()) {
                    r.setField(field);
                }
            }
        }
    }


    public List<CreateFormResponseDTO> getAllFormsByEventId(Long eventId , Users user) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        if(event.getRegistrationForms() == null || event.getRegistrationForms().isEmpty()) {
            throw new IllegalArgumentException("No forms found for this event");
        }


        if(!eventRoleService.hasRole(user.getId() , eventId , EventRoleType.ADMIN) ) {
            throw new IllegalArgumentException("User does not have permission to view All forms for this event");
        }


        List<RegistrationForm> forms = registrationFormRepo.findByEvent(event);
        return registrationFormMapper.toResponseDTOList(forms);
    }

    public CreateFormResponseDTO updateForm(Long eventId, Long formId, UpdateFormRequestDTO updateRequest, Users currentUser) {

        eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        RegistrationForm existingForm = registrationFormRepo.findById(formId)
                .orElseThrow(() -> new IllegalArgumentException("Form not found with id: " + formId));

        if (!existingForm.getIsActive()) {
            throw new IllegalArgumentException("Cannot update Deleted Form Please create a new form instead.");
        }

        if (!existingForm.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("Form does not belong to the specified event");
        }

        if(!eventRoleService.hasAdministrativeRole(currentUser.getId(), eventId) ) {
            throw new IllegalArgumentException("User does not have permission to view All forms for this event");
        }


        if (updateRequest.getTitle() != null) {
            existingForm.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getFields() != null) {
            updateFormFields(existingForm, updateRequest.getFields());
        }

        RegistrationForm savedForm = registrationFormRepo.save(existingForm);
        return registrationFormMapper.toResponseDTO(savedForm);
    }

    private void updateFormFields(RegistrationForm existingForm, List<UpdateFormRequestDTO.UpdateFormFieldDTO> fieldUpdates) {
        // Get existing fields
        List<FormField> existingFields = existingForm.getFields();
        if (existingFields == null) {
            existingFields = new ArrayList<>();
            existingForm.setFields(existingFields);
        }

        // Create a map of existing fields by ID for quick lookup
        Map<Long, FormField> existingFieldMap = existingFields.stream()
                .filter(field -> field.getId() != null)
                .collect(Collectors.toMap(FormField::getId, field -> field));

        // Clear existing fields list to rebuild it
        existingFields.clear();

        // Process field updates
        for (int i = 0; i < fieldUpdates.size(); i++) {
            UpdateFormRequestDTO.UpdateFormFieldDTO fieldUpdate = fieldUpdates.get(i);
            FormField field;

            if (fieldUpdate.getId() != null && existingFieldMap.containsKey(fieldUpdate.getId())) {
                // Update existing field
                field = existingFieldMap.get(fieldUpdate.getId());
                updateFieldProperties(field, fieldUpdate);
            } else {
                // Create new field
                field = new FormField();
                updateFieldProperties(field, fieldUpdate);
                field.setForm(existingForm);
            }

            // Set order index
            if (fieldUpdate.getOrderIndex() >= 0) {
                field.setOrderIndex(fieldUpdate.getOrderIndex());
            } else {
                field.setOrderIndex(i);
            }

            existingFields.add(field);
        }

        // Validate updated fields
        validateFields(existingFields);
    }


    private void updateFieldProperties(FormField field, UpdateFormRequestDTO.UpdateFormFieldDTO fieldUpdate) {
        if (fieldUpdate.getLabel() != null) {
            field.setLabel(fieldUpdate.getLabel());
        }
        if (fieldUpdate.getType() != null) {
            field.setType(FieldType.valueOf(fieldUpdate.getType()));
        }
        field.setRequired(fieldUpdate.isRequired());
        if (fieldUpdate.getPlaceholder() != null) {
            field.setPlaceholder(fieldUpdate.getPlaceholder());
        }
        if (fieldUpdate.getHelpText() != null) {
            field.setHelpText(fieldUpdate.getHelpText());
        }
        if (fieldUpdate.getOptions() != null) {
            field.setOptions(fieldUpdate.getOptions());
        }
    }



    public void deleteForm(Long eventId, Long formId, Users currentUser) {
        eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        RegistrationForm existingForm = registrationFormRepo.findById(formId)
                .orElseThrow(() -> new IllegalArgumentException("Form not found with id: " + formId));

        if (!existingForm.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("Form does not belong to the specified event");
        }

        if (!eventRoleService.hasAdministrativeRole(currentUser.getId(), eventId)) {
            throw new IllegalArgumentException("User does not have permission to delete forms for this event");
        }

        existingForm.setIsActive(false);
        registrationFormRepo.save(existingForm);

        log.info("Form {} soft deleted for event {} by user {}", formId, eventId, currentUser.getId());
    }

    public CreateFormResponseDTO getActiveForm(Long eventId, Users currentUser) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        RegistrationForm activeForm = registrationFormRepo.findByEventAndIsActiveTrue(event)
                .orElseThrow(() -> new IllegalArgumentException("No active form found for this event"));

        log.info("Retrieved active form for event {} by user {}", eventId, currentUser.getId());
        return registrationFormMapper.toResponseDTO(activeForm);
    }

}
