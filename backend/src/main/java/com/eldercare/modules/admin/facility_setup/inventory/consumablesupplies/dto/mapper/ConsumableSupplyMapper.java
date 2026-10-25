package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response.ConsumableSupplyResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.entity.ConsumableSupplyEntity;

@Mapper(componentModel = "spring")
public interface ConsumableSupplyMapper {

    ConsumableSupplyResponse toResponse(ConsumableSupplyEntity entity);

    @Mapping(target = "id", ignore = true)
    ConsumableSupplyEntity toEntity(ConsumableSupplyCreateRequest request);

}
