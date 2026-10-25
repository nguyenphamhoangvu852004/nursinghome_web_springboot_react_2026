package com.eldercare.modules.admin.user_management.role.service;

import com.eldercare.modules.admin.user_management.role.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getRoleList();

}
