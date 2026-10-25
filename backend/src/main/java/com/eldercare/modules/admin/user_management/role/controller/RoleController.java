package com.eldercare.modules.admin.user_management.role.controller;

import com.eldercare.modules.admin.user_management.role.dto.response.RoleResponse;
import com.eldercare.modules.admin.user_management.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoleList() {
        return ResponseEntity.ok(roleService.getRoleList());
    }
}
