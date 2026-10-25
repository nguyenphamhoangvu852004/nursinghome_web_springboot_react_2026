package com.eldercare.modules.admin.service.impl;

import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.AuditLogEntity;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.AuditLogRepository;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.PhiAccessLogEntity;
import com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail.PhiAccessLogRepository;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.admin.service.AdminService;
import com.eldercare.modules.security.SessionDetails;
import com.eldercare.modules.security.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PhiAccessLogRepository phiAccessLogRepository;

    @Autowired
    private SessionStore sessionStore;

    @Override
    public List<Map<String, Object>> getUsers() {
        List<UserEntity> users = userRepository.findAllByIsDeletedFalse();
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (UserEntity user : users) {
            responseList.add(Map.of(
                "id", user.getId(),
                "name", user.getFirstName() + " " + user.getLastName(),
                "email", user.getEmail(),
                "status", user.getStatus()
            ));
        }
        return responseList;
    }

    @Override
    public Map<String, Object> getAuditLogs(
            String tableName,
            String recordId,
            Long performedBy,
            String action,
            String dateFrom,
            String dateTo,
            int page,
            int pageSize
    ) {
        Specification<AuditLogEntity> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (tableName != null && !tableName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("tableName"), tableName.trim()));
            }
            if (recordId != null && !recordId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("recordId"), recordId.trim()));
            }
            if (performedBy != null) {
                predicates.add(cb.equal(root.get("performedBy"), performedBy));
            }
            if (action != null && !action.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("action"), action.trim()));
            }
            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                try {
                    OffsetDateTime from = OffsetDateTime.parse(dateFrom.trim());
                    predicates.add(cb.greaterThanOrEqualTo(root.get("performedAt"), from));
                } catch (Exception e) {}
            }
            if (dateTo != null && !dateTo.trim().isEmpty()) {
                try {
                    OffsetDateTime to = OffsetDateTime.parse(dateTo.trim());
                    predicates.add(cb.lessThanOrEqualTo(root.get("performedAt"), to));
                } catch (Exception e) {}
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        int jpaPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(jpaPage, pageSize, Sort.by(Sort.Direction.DESC, "performedAt"));
        Page<AuditLogEntity> resultPage = auditLogRepository.findAll(spec, pageable);

        Map<String, Object> pagination = Map.of(
            "page", page,
            "pageSize", pageSize,
            "totalItems", resultPage.getTotalElements(),
            "totalPages", Math.max(1, resultPage.getTotalPages())
        );

        return Map.of("data", resultPage.getContent(), "page", pagination);
    }

    @Override
    public Map<String, Object> getPHIAccessLogs(
            String tableName,
            String recordId,
            Long accessedBy,
            String accessType,
            int page,
            int pageSize
    ) {
        Specification<PhiAccessLogEntity> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (tableName != null && !tableName.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("tableName"), tableName.trim()));
            }
            if (recordId != null && !recordId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("recordId"), recordId.trim()));
            }
            if (accessedBy != null) {
                predicates.add(cb.equal(root.get("accessedBy"), accessedBy));
            }
            if (accessType != null && !accessType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("accessType"), accessType.trim()));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        int jpaPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(jpaPage, pageSize, Sort.by(Sort.Direction.DESC, "accessedAt"));
        Page<PhiAccessLogEntity> resultPage = phiAccessLogRepository.findAll(spec, pageable);

        Map<String, Object> pagination = Map.of(
            "page", page,
            "pageSize", pageSize,
            "totalItems", resultPage.getTotalElements(),
            "totalPages", Math.max(1, resultPage.getTotalPages())
        );

        return Map.of("data", resultPage.getContent(), "page", pagination);
    }

    @Override
    public List<Map<String, Object>> getActiveSessions() {
        List<SessionDetails> sessions = sessionStore.getAllSessions();
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (SessionDetails details : sessions) {
            responseList.add(Map.of(
                "session_id", details.getSessionId(),
                "user_id", details.getUserId(),
                "login_at", details.getLoginAt().toString(),
                "last_activity_at", details.getLastActivityAt().toString(),
                "status", details.getStatus()
            ));
        }
        return responseList;
    }

    @Override
    public void forceLogout(String sessionId) {
        boolean found = sessionStore.forceLogout(sessionId);
        if (!found) {
            throw new ResourceNotFoundException("Session not found or already logged out");
        }
    }

    @Override
    public Map<String, Object> changeUserStatus(Long userId, String status) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        UserEntity user = userOpt.get();
        user.setStatus(status.toUpperCase());
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        return Map.of(
            "id", userId,
            "status", user.getStatus(),
            "updated_at", user.getUpdatedAt().toString()
        );
    }
}
