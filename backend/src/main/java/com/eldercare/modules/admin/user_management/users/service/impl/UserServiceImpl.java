package com.eldercare.modules.admin.user_management.users.service.impl;

import com.eldercare.exception.ConflictException;
import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.role.repository.RoleRepository;
import com.eldercare.modules.admin.user_management.users.dto.request.ChangeStatusRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.CreateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.UpdateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.response.UserDetailResponse;
import com.eldercare.modules.admin.user_management.users.dto.response.UserResponse;
import com.eldercare.modules.admin.user_management.users.mapper.UserMapper;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.admin.user_management.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String STATUS_INVITED = "INVITED";
    private static final String PASSWORD_PENDING = "PENDING_SETUP";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(
            String keyword,
            Long roleId,
            String status,
            Pageable pageable
    ) {

        return userRepository.search(keyword, roleId, status, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(Long id) {

        UserEntity user = findActiveUser(id);

        return userMapper.toDetailResponse(user, null, null);
    }

    @Override
    public UserDetailResponse createUser(CreateUserRequest request) {

        validateEmail(request.email());

        RoleEntity role = findRole(request.roleId());

        UserEntity user = userMapper.toEntity(request, role);

        user.setEmployeeCode(generateEmployeeCode());
        user.setPasswordHash(PASSWORD_PENDING);
        user.setStatus(STATUS_INVITED);
        user.setMfaEnabled(false);

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toDetailResponse(
                savedUser,
                request.facilityId(),
                null
        );
    }

    @Override
    public UserDetailResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        UserEntity user = findActiveUser(id);

        validateEmailForUpdate(request.email(), id);

        RoleEntity role = findRole(request.roleId());

        userMapper.updateEntity(user, request, role);

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toDetailResponse(
                savedUser,
                request.facilityId(),
                null
        );
    }

    @Override
    public UserDetailResponse changeUserStatus(
            Long id,
            ChangeStatusRequest request
    ) {

        UserEntity user = findActiveUser(id);

        user.setStatus(request.status());

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toDetailResponse(
                savedUser,
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveCnas() {
        // Find role by name "CNA"
        RoleEntity cnaRole = roleRepository.findByRoleName("CNA")
                .orElse(null);

        if (cnaRole == null) {
            // fallback: return all active users if CNA role is not seeded yet, or return empty
            return userRepository.findAllByIsDeletedFalse().stream()
                    .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                    .map(userMapper::toResponse)
                    .toList();
        }

        // Try to find users with CNA role
        List<UserResponse> cnas = userRepository.findByRoleIdAndStatusAndIsDeletedFalse(cnaRole.getId(), "ACTIVE").stream()
                .map(userMapper::toResponse)
                .toList();

        // If no user has CNA role specifically, fallback to return all active users in the app
        if (cnas.isEmpty()) {
            return userRepository.findAllByIsDeletedFalse().stream()
                    .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                    .map(userMapper::toResponse)
                    .toList();
        }

        return cnas;
    }

    // ==============================
    // Validation
    // ==============================

    private void validateEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(
                    "Email already exists: " + email
            );
        }
    }

    private void validateEmailForUpdate(
            String email,
            Long id
    ) {

        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new ConflictException(
                    "Email already exists: " + email
            );
        }
    }

    // ==============================
    // Finder
    // ==============================

    private UserEntity findActiveUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found: " + id));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new NotFoundException("User not found: " + id);
        }

        return user;
    }

    private RoleEntity findRole(Long roleId) {

        return roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new NotFoundException("Role not found: " + roleId));
    }

    // ==============================
    // Helper
    // ==============================

    private String generateEmployeeCode() {

        return "EMP-" +
                new BigInteger(20, RANDOM)
                        .toString(36)
                        .toUpperCase();
    }

}