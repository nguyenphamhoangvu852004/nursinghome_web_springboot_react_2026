package com.eldercare.modules.admin.controller;

import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.AuditLogEntity;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.PhiAccessLogEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.AuditLogRepository;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.PhiAccessLogRepository;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.security.SessionDetails;
import com.eldercare.modules.security.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/security-telemetry")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private com.eldercare.modules.admin.service.AdminService adminService;

    // 0. Get Real Users list
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    // 1. Audit Logs (System Actions)
    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String table_name,
            @RequestParam(required = false) String record_id,
            @RequestParam(required = false) Long performed_by,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Map<String, Object> result = adminService.getAuditLogs(
                table_name, record_id, performed_by, action, dateFrom, dateTo, page, pageSize);
        return ResponseEntity.ok(result);
    }

    // 2. PHI Access Logs
    @GetMapping("/phi-access-logs")
    public ResponseEntity<?> getPHIAccessLogs(
            @RequestParam(required = false) String table_name,
            @RequestParam(required = false) String record_id,
            @RequestParam(required = false) Long accessed_by,
            @RequestParam(required = false) String access_type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Map<String, Object> result = adminService.getPHIAccessLogs(
                table_name, record_id, accessed_by, access_type, page, pageSize);
        return ResponseEntity.ok(result);
    }

    // 3. Active Sessions
    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(adminService.getActiveSessions());
    }

    // 4. Force Logout
    @PostMapping("/sessions/{sessionId}/force-logout")
    public ResponseEntity<?> forceLogout(@PathVariable String sessionId) {
        try {
            adminService.forceLogout(sessionId);
            return ResponseEntity.ok(Map.of(
                    "session_id", sessionId,
                    "status", "ForcedLogout"));
        } catch (com.eldercare.exception.custom.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred"));
        }
    }

    // 5. Change User Status
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> changeUserStatus(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String newStatus = body.getOrDefault("status", "INACTIVE");
        try {
            Map<String, Object> result = adminService.changeUserStatus(userId, newStatus);
            return ResponseEntity.ok(result);
        } catch (com.eldercare.exception.custom.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred"));
        }
    }
}
