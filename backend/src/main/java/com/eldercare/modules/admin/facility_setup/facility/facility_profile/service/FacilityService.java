package com.eldercare.modules.admin.facility_setup.facility.facility_profile.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityCreateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityUpdateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilityResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilitySelectResponse;

import java.util.List;

public interface FacilityService {
    PagedResponse<List<FacilityResponse>> getFacilities(int page, int size, String search);

    FacilityResponse createFacility(FacilityCreateRequest request);

    FacilityResponse getFacilityInfo(Long facilityId);

    FacilityResponse updateFacilityInfo(Long facilityId, FacilityUpdateRequest request);

    List<FacilitySelectResponse> getFacilitiesForSelect();
}
