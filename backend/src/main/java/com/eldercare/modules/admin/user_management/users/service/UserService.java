package com.eldercare.modules.admin.user_management.users.service;

import com.eldercare.modules.admin.user_management.users.dto.request.ChangeStatusRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.CreateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.UpdateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.response.UserDetailResponse;
import com.eldercare.modules.admin.user_management.users.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserResponse> getUsers(
            String keyword,
            Long roleId,
            String status,
            Pageable pageable
    );

    UserDetailResponse getUserById(Long id);

    UserDetailResponse createUser(CreateUserRequest request);

    UserDetailResponse updateUser(Long id, UpdateUserRequest request);

    UserDetailResponse changeUserStatus(Long id, ChangeStatusRequest request);

    List<UserResponse> getActiveCnas();
}