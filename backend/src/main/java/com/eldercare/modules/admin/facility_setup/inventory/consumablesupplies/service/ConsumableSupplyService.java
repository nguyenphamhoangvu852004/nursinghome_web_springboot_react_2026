package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service;

import java.util.List;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response.ConsumableSupplyResponse;

public interface ConsumableSupplyService {
    PagedResponse<List<ConsumableSupplyResponse>> getAllConsumableSupplies(int page, int size);

    ConsumableSupplyResponse createConsumableSupply(ConsumableSupplyCreateRequest consumableSupplyRequest);

    PagedResponse<List<ConsumableSupplyResponse>> getLowStockSupplies(int page, int size);

    ConsumableSupplyResponse getConsumableSupplyById(Long id);

    ConsumableSupplyResponse updateConsumableSupply(Long id, ConsumableSupplyUpdateRequest consumableSupplyRequest);
    
    void deleteConsumableSupply(Long id);
}
