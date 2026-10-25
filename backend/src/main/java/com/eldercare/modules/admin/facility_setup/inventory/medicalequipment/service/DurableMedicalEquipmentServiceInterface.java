package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service;

import java.util.List;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentChangeStatusRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response.DurableMedicalEquipmentResponse;

public interface DurableMedicalEquipmentServiceInterface {

    PagedResponse<List<DurableMedicalEquipmentResponse>> getAllDurableMedicalEquipment(int page, int size);

    DurableMedicalEquipmentResponse createEquipment(DurableMedicalEquipmentCreateRequest durableMedicalEquipmentRequest);

    DurableMedicalEquipmentResponse getEquipmentById(Long id);

    DurableMedicalEquipmentResponse updateEquipment(Long id,
            DurableMedicalEquipmentUpdateRequest durableMedicalEquipmentRequest);

    void deleteEquipment(Long id);

    DurableMedicalEquipmentResponse patchEquipmentStatus(long id,
            DurableMedicalEquipmentChangeStatusRequest durableMedicalEquipmentRequest);

//     DurableMedicalEquipmentResponse assignEquipmentForUser(Long id,
//             DurableMedicalEquipmentCreateRequest durableMedicalEquipmentRequest);

//     DurableMedicalEquipmentResponse unassignEquipmentForUser(Long id);
}
