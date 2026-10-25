package com.eldercare.modules.resident_intake.family_contacts.controller;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactListResponseContainer;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentByContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // API 28
    @GetMapping
    @PreAuthorize("""
            hasAnyRole(
                'Admission_Staff',
                'Facility_Manager',
                'Accountant/Billing_Staff',
                'System_Administrator'
            )
            """)
    public ResponseEntity<ApiResponse<ContactListResponseContainer>> getContacts(
            @RequestParam(required = false) String search,
            @RequestParam(name = "include_deleted", defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize) {

        ContactListResponseContainer data = contactService.getContacts(search, includeDeleted, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // API 29
    @GetMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'Admission_Staff',
                'Facility_Manager',
                'System_Administrator'
            )
            """)
    public ResponseEntity<ApiResponse<Map<String, ContactResponse>>> getContactById(@PathVariable Long id) {
        ContactResponse contact = contactService.getContactById(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("contact", contact)));
    }

    // API 30
    @PostMapping
    @PreAuthorize("""
            hasAnyRole(
                'Admission_Staff',
                'System_Administrator'
            )
            """)
    public ResponseEntity<ApiResponse<Map<String, ContactResponse>>> createContact(
            @Valid @RequestBody ContactCreateRequest request) {

        ContactResponse created = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Success", Map.of("contact", created)));
    }

    // API 31
    @PatchMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'Admission_Staff',
                'System_Administrator'
            )
            """)
    public ResponseEntity<ApiResponse<Map<String, ContactResponse>>> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactUpdateRequest request) {

        ContactResponse updated = contactService.updateContact(id, request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("contact", updated)));
    }

    // API 32
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('System_Administrator')")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // API 33
    @GetMapping("/{id}/residents")
    @PreAuthorize("""
            hasAnyRole(
                'Admission_Staff',
                'Facility_Manager',
                'System_Administrator'
            )
            """)
    public ResponseEntity<ApiResponse<List<ResidentByContactResponse>>> getResidentsByContact(
            @PathVariable Long id) {

        List<ResidentByContactResponse> residents = contactService.getResidentsByContact(id);
        return ResponseEntity.ok(ApiResponse.success(residents));
    }
}