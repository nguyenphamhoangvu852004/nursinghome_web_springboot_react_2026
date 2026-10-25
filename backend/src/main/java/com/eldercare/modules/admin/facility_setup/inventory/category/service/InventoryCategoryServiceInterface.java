package com.eldercare.modules.admin.facility_setup.inventory.category.service;

import java.util.List;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;

public interface InventoryCategoryServiceInterface {
    public PagedResponse<List<InventoryCategoryResponse>> getInventoryCategoryList(int size, int page);

    public InventoryCategoryResponse createInventoryCategory(InventoryCategoryRequest request);

    public InventoryCategoryResponse getInventoryCategoryById(long id);

    public InventoryCategoryResponse updateInventoryCategory(long id, InventoryCategoryRequest request);

    public void deleteInventoryCategory(long id);
}
