package com.eldercare.modules.admin.facility_setup.facility.facility_profile.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.exception.custom.BadRequestException;
import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityCreateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityUpdateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilityResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilitySelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.mapper.FacilityMapper;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.service.impl.FacilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FacilityServiceImpl using Equivalence Partitioning technique.
 *
 * Equivalence Partitioning divides inputs into valid and invalid partitions.
 * Each partition should produce the same behavior, so one representative value is tested per partition.
 *
 * Partitions identified per method:
 *
 * getFacilities(page, size, search):
 *   EP1: search = null         -> findAll() path
 *   EP2: search = ""           -> findAll() path (blank/empty treated same as null)
 *   EP3: search = "   "        -> findAll() path (whitespace-only)
 *   EP4: search = "validText"  -> filtered search path
 *
 * createFacility(request):
 *   EP1: facilityCode already exists  -> BadRequestException
 *   EP2: facilityCode does not exist  -> saves and returns response
 *
 * getFacilityInfo(facilityId):
 *   EP1: facilityId exists     -> returns FacilityResponse
 *   EP2: facilityId not found  -> ResourceNotFoundException
 *
 * updateFacilityInfo(facilityId, request):
 *   EP1: facilityId exists     -> updates and returns response
 *   EP2: facilityId not found  -> ResourceNotFoundException
 *
 * getFacilitiesForSelect():
 *   EP1: no active facilities  -> returns empty list
 *   EP2: active facilities exist -> returns list of FacilitySelectResponse
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityService - Equivalence Partitioning Tests")
class FacilityServiceTests {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private FacilityMapper facilityMapper;

    @InjectMocks
    private FacilityServiceImpl facilityService;

    private FacilityEntity facilityEntity;
    private FacilityResponse facilityResponse;

    @BeforeEach
    void setUp() {
        facilityEntity = FacilityEntity.builder()
                .id(1L)
                .facilityCode("FAC-001")
                .name("Sunrise Care Home")
                .licenseNumber("LIC-2024-001")
                .targetState("CA")
                .phoneNumber("555-0100")
                .build();

        facilityResponse = new FacilityResponse();
        facilityResponse.setId(1L);
        facilityResponse.setFacilityCode("FAC-001");
        facilityResponse.setName("Sunrise Care Home");
    }

    // =========================================================================
    // getFacilities
    // =========================================================================

    @Nested
    @DisplayName("getFacilities - Equivalence Partitioning")
    class GetFacilitiesTests {

        /**
         * EP1: search = null
         * Expected: delegates to findAll(pageable), no search filter applied
         */
        @Test
        @DisplayName("EP1: search is null -> returns all facilities without filter")
        void getFacilities_SearchNull_ReturnsAllFacilities() {
            // Arrange
            int page = 0, size = 10;
            Page<FacilityEntity> pagedResult = new PageImpl<>(List.of(facilityEntity));
            when(facilityRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            PagedResponse<List<FacilityResponse>> result = facilityService.getFacilities(page, size, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(facilityRepository).findAll(any(Pageable.class));
            verify(facilityRepository, never())
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            any(), any(), any(), any());
        }

        /**
         * EP2: search = "" (empty string)
         * Expected: treated same as null, delegates to findAll
         */
        @Test
        @DisplayName("EP2: search is empty string -> returns all facilities without filter")
        void getFacilities_SearchEmpty_ReturnsAllFacilities() {
            // Arrange
            int page = 0, size = 10;
            Page<FacilityEntity> pagedResult = new PageImpl<>(List.of(facilityEntity));
            when(facilityRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            PagedResponse<List<FacilityResponse>> result = facilityService.getFacilities(page, size, "");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(facilityRepository).findAll(any(Pageable.class));
            verify(facilityRepository, never())
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            any(), any(), any(), any());
        }

        /**
         * EP3: search = "   " (whitespace only)
         * Expected: treated same as null (after trim().isEmpty()), delegates to findAll
         */
        @Test
        @DisplayName("EP3: search is whitespace only -> returns all facilities without filter")
        void getFacilities_SearchWhitespace_ReturnsAllFacilities() {
            // Arrange
            int page = 0, size = 10;
            Page<FacilityEntity> pagedResult = new PageImpl<>(List.of(facilityEntity));
            when(facilityRepository.findAll(any(Pageable.class))).thenReturn(pagedResult);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            PagedResponse<List<FacilityResponse>> result = facilityService.getFacilities(page, size, "   ");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(facilityRepository).findAll(any(Pageable.class));
            verify(facilityRepository, never())
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            any(), any(), any(), any());
        }

        /**
         * EP4: search = "Sunrise" (non-blank string)
         * Expected: delegates to search repository method with the keyword
         */
        @Test
        @DisplayName("EP4: search is valid non-blank string -> returns filtered facilities")
        void getFacilities_SearchValidKeyword_ReturnsFilteredFacilities() {
            // Arrange
            int page = 0, size = 10;
            String keyword = "Sunrise";
            Page<FacilityEntity> pagedResult = new PageImpl<>(List.of(facilityEntity));
            when(facilityRepository
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            eq(keyword), eq(keyword), eq(keyword), any(Pageable.class)))
                    .thenReturn(pagedResult);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            PagedResponse<List<FacilityResponse>> result = facilityService.getFacilities(page, size, keyword);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(facilityRepository)
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            eq(keyword), eq(keyword), eq(keyword), any(Pageable.class));
            verify(facilityRepository, never()).findAll(any(Pageable.class));
        }
    }

    // =========================================================================
    // createFacility
    // =========================================================================

    @Nested
    @DisplayName("createFacility - Equivalence Partitioning")
    class CreateFacilityTests {

        /**
         * EP1: facilityCode already exists in repository
         * Expected: throws BadRequestException
         */
        @Test
        @DisplayName("EP1: facilityCode already exists -> throws BadRequestException")
        void createFacility_DuplicateFacilityCode_ThrowsBadRequestException() {
            // Arrange
            FacilityCreateRequest request = new FacilityCreateRequest();
            request.setFacilityCode("FAC-001");
            request.setName("Another Facility");

            when(facilityRepository.findByFacilityCode("FAC-001"))
                    .thenReturn(Optional.of(facilityEntity));

            // Act & Assert
            assertThatThrownBy(() -> facilityService.createFacility(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Facility code already exists");

            verify(facilityRepository, never()).save(any());
        }

        /**
         * EP2: facilityCode is unique (not present in repository)
         * Expected: saves entity and returns FacilityResponse
         */
        @Test
        @DisplayName("EP2: facilityCode is unique -> saves and returns FacilityResponse")
        void createFacility_UniqueFacilityCode_SavesAndReturnsFacilityResponse() {
            // Arrange
            FacilityCreateRequest request = new FacilityCreateRequest();
            request.setFacilityCode("FAC-999");
            request.setName("New Facility");

            when(facilityRepository.findByFacilityCode("FAC-999")).thenReturn(Optional.empty());
            when(facilityMapper.toEntity(request)).thenReturn(facilityEntity);
            when(facilityRepository.save(facilityEntity)).thenReturn(facilityEntity);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            FacilityResponse result = facilityService.createFacility(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(facilityRepository).save(facilityEntity);
        }
    }

    // =========================================================================
    // getFacilityInfo
    // =========================================================================

    @Nested
    @DisplayName("getFacilityInfo - Equivalence Partitioning")
    class GetFacilityInfoTests {

        /**
         * EP1: facilityId corresponds to an existing facility
         * Expected: returns FacilityResponse
         */
        @Test
        @DisplayName("EP1: facilityId exists -> returns FacilityResponse")
        void getFacilityInfo_ExistingId_ReturnsFacilityResponse() {
            // Arrange
            Long facilityId = 1L;
            when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facilityEntity));
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            FacilityResponse result = facilityService.getFacilityInfo(facilityId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(facilityId);
        }

        /**
         * EP2: facilityId does not exist in repository
         * Expected: throws ResourceNotFoundException
         */
        @Test
        @DisplayName("EP2: facilityId not found -> throws ResourceNotFoundException")
        void getFacilityInfo_NonExistingId_ThrowsResourceNotFoundException() {
            // Arrange
            Long nonExistingId = 999L;
            when(facilityRepository.findById(nonExistingId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> facilityService.getFacilityInfo(nonExistingId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Facility not found");
        }
    }

    // =========================================================================
    // updateFacilityInfo
    // =========================================================================

    @Nested
    @DisplayName("updateFacilityInfo - Equivalence Partitioning")
    class UpdateFacilityInfoTests {

        /**
         * EP1: facilityId exists -> update succeeds and returns updated FacilityResponse
         */
        @Test
        @DisplayName("EP1: facilityId exists -> updates entity and returns updated FacilityResponse")
        void updateFacilityInfo_ExistingId_UpdatesAndReturnsFacilityResponse() {
            // Arrange
            Long facilityId = 1L;
            FacilityUpdateRequest request = new FacilityUpdateRequest();
            request.setName("Updated Facility Name");

            when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facilityEntity));
            doNothing().when(facilityMapper).updateEntity(facilityEntity, request);
            when(facilityRepository.save(facilityEntity)).thenReturn(facilityEntity);
            when(facilityMapper.toResponse(facilityEntity)).thenReturn(facilityResponse);

            // Act
            FacilityResponse result = facilityService.updateFacilityInfo(facilityId, request);

            // Assert
            assertThat(result).isNotNull();
            verify(facilityMapper).updateEntity(facilityEntity, request);
            verify(facilityRepository).save(facilityEntity);
        }

        /**
         * EP2: facilityId does not exist -> throws ResourceNotFoundException
         */
        @Test
        @DisplayName("EP2: facilityId not found -> throws ResourceNotFoundException")
        void updateFacilityInfo_NonExistingId_ThrowsResourceNotFoundException() {
            // Arrange
            Long nonExistingId = 999L;
            FacilityUpdateRequest request = new FacilityUpdateRequest();
            request.setName("Updated Name");

            when(facilityRepository.findById(nonExistingId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> facilityService.updateFacilityInfo(nonExistingId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Facility not found");

            verify(facilityRepository, never()).save(any());
        }
    }

    // =========================================================================
    // getFacilitiesForSelect
    // =========================================================================

    @Nested
    @DisplayName("getFacilitiesForSelect - Equivalence Partitioning")
    class GetFacilitiesForSelectTests {

        /**
         * EP1: no active (non-deleted) facilities exist
         * Expected: returns empty list
         */
        @Test
        @DisplayName("EP1: no active facilities -> returns empty list")
        void getFacilitiesForSelect_NoActiveFacilities_ReturnsEmptyList() {
            // Arrange
            when(facilityRepository.findByIsDeletedFalse()).thenReturn(Collections.emptyList());

            // Act
            List<FacilitySelectResponse> result = facilityService.getFacilitiesForSelect();

            // Assert
            assertThat(result).isNotNull().isEmpty();
        }

        /**
         * EP2: active facilities exist
         * Expected: returns list of FacilitySelectResponse with id and name
         */
        @Test
        @DisplayName("EP2: active facilities exist -> returns list of FacilitySelectResponse")
        void getFacilitiesForSelect_ActiveFacilitiesExist_ReturnsMappedList() {
            // Arrange
            when(facilityRepository.findByIsDeletedFalse()).thenReturn(List.of(facilityEntity));

            // Act
            List<FacilitySelectResponse> result = facilityService.getFacilitiesForSelect();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getName()).isEqualTo("Sunrise Care Home");
        }
    }
}
