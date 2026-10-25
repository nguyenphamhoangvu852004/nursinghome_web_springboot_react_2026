package com.eldercare.modules.admin.user_management.users.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.modules.admin.user_management.users.dto.request.ChangeStatusRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.CreateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.UpdateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.response.UserDetailResponse;
import com.eldercare.modules.admin.user_management.users.dto.response.UserResponse;
import com.eldercare.modules.admin.user_management.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @GetMapping(RouteConstants.ADMIN_API_PREFIX + "/users")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        Page<UserResponse> users = userService.getUsers(
                keyword,
                roleId,
                status,
                pageable);

        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('DON')")
    @GetMapping(RouteConstants.API_USERS_CNAS)
    public ResponseEntity<List<UserResponse>> getActiveCnas() {
        List<UserResponse> cnas = userService.getActiveCnas();
        return ResponseEntity.ok(cnas);
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @GetMapping(RouteConstants.ADMIN_API_PREFIX + "/users/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(
            @PathVariable Long id) {

        UserDetailResponse user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PostMapping(RouteConstants.ADMIN_API_PREFIX + "/users")
    public ResponseEntity<UserDetailResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserDetailResponse createdUser = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PutMapping(RouteConstants.ADMIN_API_PREFIX + "/users/{id}")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserDetailResponse updatedUser = userService.updateUser(id, request);

        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PatchMapping(RouteConstants.ADMIN_API_PREFIX + "/users/{id}/status")
    public ResponseEntity<UserDetailResponse> changeUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {

        UserDetailResponse updatedUser = userService.changeUserStatus(id, request);

        return ResponseEntity.ok(updatedUser);
    }
}