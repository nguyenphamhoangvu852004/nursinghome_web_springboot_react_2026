package com.eldercare.modules.security;

import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    // Maps token -> SessionDetails
    private final Map<String, SessionDetails> activeSessions = new ConcurrentHashMap<>();

    public void createSession(String token, String sessionId, Long userId, String email, String role, String name) {
        SessionDetails details = new SessionDetails(
            sessionId,
            userId,
            email,
            role,
            name,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "Active"
        );
        activeSessions.put(token, details);
    }

    public Optional<SessionDetails> getSession(String token) {
        SessionDetails details = activeSessions.get(token);
        if (details != null && "Active".equals(details.getStatus())) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public void updateActivity(String token) {
        SessionDetails details = activeSessions.get(token);
        if (details != null) {
            details.setLastActivityAt(OffsetDateTime.now());
        }
    }

    public void removeSession(String token) {
        activeSessions.remove(token);
    }

    public List<SessionDetails> getAllSessions() {
        List<SessionDetails> list = new ArrayList<>();
        for (SessionDetails details : activeSessions.values()) {
            if ("Active".equals(details.getStatus())) {
                list.add(details);
            }
        }
        return list;
    }

    public boolean forceLogout(String sessionId) {
        boolean found = false;
        // Mark as ForcedLogout so filter will deny access
        for (SessionDetails details : activeSessions.values()) {
            if (details.getSessionId().equals(sessionId)) {
                details.setStatus("ForcedLogout");
                found = true;
            }
        }
        // Also remove them from the active sessions map
        activeSessions.entrySet().removeIf(entry -> "ForcedLogout".equals(entry.getValue().getStatus()));
        return found;
    }
}
