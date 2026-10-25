package com.eldercare.modules.admin.user_management.users.mapper;

import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.users.dto.request.CreateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.request.UpdateUserRequest;
import com.eldercare.modules.admin.user_management.users.dto.response.UserDetailResponse;
import com.eldercare.modules.admin.user_management.users.dto.response.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper Ä‘á»ƒ chuyá»ƒn Ä‘á»•i giá»¯a UserEntity vÃ  cÃ¡c DTO
 */
@Component
public class UserMapper {

    public UserEntity toEntity(CreateUserRequest request, RoleEntity role) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(request.firstName());
        entity.setMiddleName(request.middleName());
        entity.setLastName(request.lastName());
        entity.setEmail(request.email());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setRole(role);
        return entity;
    }

    public void updateEntity(UserEntity entity, UpdateUserRequest request, RoleEntity role) {
        entity.setFirstName(request.firstName());
        entity.setMiddleName(request.middleName());
        entity.setLastName(request.lastName());
        entity.setEmail(request.email());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setRole(role);
    }

    public UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
            entity.getId(),
            buildFullName(entity),
            entity.getEmail(),
            entity.getPhoneNumber(),
            entity.getRole().getRoleName(),
            entity.getStatus(),
            entity.getMfaEnabled(),
            entity.getLastLoginAt()
        );
    }

    public UserDetailResponse toDetailResponse(UserEntity entity, Long facilityId, String facilityName) {
        return new UserDetailResponse(
            entity.getId(),
            entity.getFirstName(),
            entity.getMiddleName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getPhoneNumber(),
            entity.getRole().getId(),
            entity.getRole().getRoleName(),
            entity.getStatus(),
            facilityId,
            facilityName
        );
    }

    private String buildFullName(UserEntity entity) {
        return String.join(" ",
                entity.getFirstName(),
                entity.getMiddleName() == null ? "" : entity.getMiddleName(),
                entity.getLastName()
        ).replaceAll("\\s+", " ").trim();
    }
}