package com.eldercare.modules.admin.user_management;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    @Id
    @Column(name = "action_code", length = 100)
    private String actionCode;

    @Column(name = "is_phi_sensitive", nullable = false)
    private Boolean isPhiSensitive = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
