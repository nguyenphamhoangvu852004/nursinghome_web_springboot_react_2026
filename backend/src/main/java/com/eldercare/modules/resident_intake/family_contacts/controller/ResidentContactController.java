package com.eldercare.modules.resident_intake.family_contacts.controller;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentQuickContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.service.ResidentContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/residents/{residentId}/contacts")
@RequiredArgsConstructor
public class ResidentContactController {

        private final ResidentContactService residentContactService;

        // API 34
        @GetMapping
        public ResponseEntity<List<ResidentContactResponse>> getResidentContacts(
                        @PathVariable Long residentId) {

                return ResponseEntity.ok(residentContactService.getResidentContacts(residentId));
        }

        // API 35
        @PostMapping
        public ResponseEntity<ResidentContactResponse> createResidentContact(
                        @PathVariable Long residentId,
                        @Valid @RequestBody ResidentContactCreateRequest request) {

                ResidentContactResponse response = residentContactService.createResidentContact(residentId, request);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // API 36
        @PatchMapping("/{residentContactId}")
        public ResponseEntity<ResidentContactResponse> updateResidentContact(
                        @PathVariable Long residentId,
                        @PathVariable Long residentContactId,
                        @Valid @RequestBody ResidentContactUpdateRequest request) {

                return ResponseEntity.ok(
                                residentContactService.updateResidentContact(
                                                residentId,
                                                residentContactId,
                                                request));
        }

        // API 37
        @DeleteMapping("/{residentContactId}")
        public ResponseEntity<Void> deleteResidentContact(
                        @PathVariable Long residentId,
                        @PathVariable Long residentContactId) {

                residentContactService.deleteResidentContact(residentId, residentContactId);
                return ResponseEntity.noContent().build();
        }

        // API 38
        @GetMapping("/guarantor")
        public ResponseEntity<ApiResponse<Map<String, ResidentQuickContactResponse>>> getResidentGuarantorContact(
                        @PathVariable Long residentId) {

                ResidentQuickContactResponse contact = residentContactService.getResidentGuarantorContact(residentId);

                return ResponseEntity.ok(ApiResponse.success(Map.of("contact", contact)));
        }

        // API 39
        @GetMapping("/primary")
        public ResponseEntity<ApiResponse<Map<String, ResidentQuickContactResponse>>> getResidentPrimaryContact(
                        @PathVariable Long residentId) {

                ResidentQuickContactResponse contact = residentContactService.getResidentPrimaryContact(residentId);

                return ResponseEntity.ok(ApiResponse.success(Map.of("contact", contact)));
        }
}