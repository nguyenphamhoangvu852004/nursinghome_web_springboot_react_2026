package com.eldercare.modules.risk_incident.incident_tracking.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentResponse {
 
    private Long id;
    private ResidentResponse resident;
    private String incidentType;
    private String status;
    private Boolean automaticLockChart;
    private String location;
    private String description;
    private String witnesses;
    private Integer slaDeadlineHours;
    private SeverityResponse severity;
    private ReporterResponse reporter;
    private Boolean isLocked;
    private OffsetDateTime reportedAt;
    private Long slaCountDown;
    private List<TimelineResponse> timelines;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomResponse {

        private Long id;
        private String roomNumber;
        private String roomType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BedResponse {

        private Long id;
        private String bedNumber;
        private RoomResponse room;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResidentResponse {

        private Long id;
        private String displayName;
        private String gender;
        private BedResponse bed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeverityResponse {

        private Long id;
        private String levelName;
        private Integer slaConfigured;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReporterResponse {

        private Long id;
        private String employeeCode;
        private String displayName;
        private String email;
        private String phoneNumber;
        private String status;
        private OffsetDateTime lastLoginAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActorResponse {

        private Long id;
        private String displayName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelineResponse {

        private Long id;
        private String action;
        private String reason;
        private ActorResponse actor;
        private OffsetDateTime createdAt;
    }
}