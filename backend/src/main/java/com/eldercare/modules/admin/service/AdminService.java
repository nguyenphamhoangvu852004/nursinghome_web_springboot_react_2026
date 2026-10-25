package com.eldercare.modules.admin.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, Object>> getUsers();
    
    Map<String, Object> getAuditLogs(
            String tableName,
            String recordId,
            Long performedBy,
            String action,
            String dateFrom,
            String dateTo,
            int page,
            int pageSize
    );

    Map<String, Object> getPHIAccessLogs(
            String tableName,
            String recordId,
            Long accessedBy,
            String accessType,
            int page,
            int pageSize
    );

    List<Map<String, Object>> getActiveSessions();

    void forceLogout(String sessionId);

    Map<String, Object> changeUserStatus(Long userId, String status);
}
