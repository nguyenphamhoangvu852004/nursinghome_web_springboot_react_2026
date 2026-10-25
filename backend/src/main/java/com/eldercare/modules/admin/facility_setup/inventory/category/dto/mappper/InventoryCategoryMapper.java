package com.eldercare.modules.admin.facility_setup.inventory.category.dto.mappper;

import org.mapstruct.Mapper;

import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryCategoryMapper {
    

    @Mapping(target = "id", ignore = true)
    InventoryCategoryEntity toEntity(InventoryCategoryRequest request);


    InventoryCategoryResponse toResponse(InventoryCategoryEntity entity);
}
