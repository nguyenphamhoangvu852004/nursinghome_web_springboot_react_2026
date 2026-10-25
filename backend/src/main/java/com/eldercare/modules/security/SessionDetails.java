package com.eldercare.modules.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetails {
    private String sessionId;
    private Long userId;
    private String email;
    private String role;
    private String name;
    private OffsetDateTime loginAt;
    private OffsetDateTime lastActivityAt;
    private String status; // "Active" or "ForcedLogout"
}
