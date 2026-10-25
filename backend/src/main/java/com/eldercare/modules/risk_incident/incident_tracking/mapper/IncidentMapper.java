package com.eldercare.modules.risk_incident.incident_tracking.mapper;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.risk_incident.incident_timeline.entity.IncidentTimelineEntity;
import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentResponse;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(IncidentEntity e) {
        return IncidentResponse.builder()
                .id(e.getId())
                .resident(toResidentDto(e))
                .incidentType(e.getIncidentType().name())
                .status(e.getStatus().name())
                .automaticLockChart(e.getSeverity().getChartLockTrigger())
                .description(e.getDescription())
                .witnesses(e.getWitnesses())
                .severity(toSeverityDto(e, null))
                .isLocked(e.getResident().isChartLocked())
                .reportedAt(e.getReportedAt())
                .slaCountDown(calcSlaCountDown(e))
                .timelines(Collections.emptyList())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public IncidentResponse toDetail(IncidentEntity e, Integer slaHours, List<IncidentTimelineEntity> timelines) {
        return IncidentResponse.builder()
                .id(e.getId())
                .resident(toResidentDto(e))
                .incidentType(e.getIncidentType().name())
                .status(e.getStatus().name())
                .automaticLockChart(e.getSeverity().getChartLockTrigger())
                .description(e.getDescription())
                .witnesses(e.getWitnesses())
                .slaDeadlineHours(slaHours)
                .severity(toSeverityDto(e, slaHours))
                .reporter(toReporterDto(e.getReporter()))
                .isLocked(e.getResident().isChartLocked())
                .reportedAt(e.getReportedAt())
                .slaCountDown(calcSlaCountDown(e))
                .timelines(toTimelineDtos(timelines))
                .createdAt(e.getCreatedAt())
                .build();
    }

    private IncidentResponse.ResidentResponse toResidentDto(IncidentEntity e) {
        ResidentEntity resident = e.getResident();
        var bed = resident.getBed();

        return IncidentResponse.ResidentResponse.builder()
                .id(resident.getId())
                .displayName(buildResidentName(resident))
                .gender(resident.getGender())
                .bed(bed == null ? null :
                        IncidentResponse.BedResponse.builder()
                                .id(bed.getId())
                                .bedNumber(bed.getBedNumber())
                                .room(bed.getRoom() == null ? null :
                                        IncidentResponse.RoomResponse.builder()
                                                .id(bed.getRoom().getId())
                                                .roomNumber(bed.getRoom().getRoomNumber())
                                                .roomType(bed.getRoom().getRoomType().name())
                                                .build())
                                .build())
                .build();
    }

    private IncidentResponse.SeverityResponse toSeverityDto(IncidentEntity e, Integer sla) {
        return IncidentResponse.SeverityResponse.builder()
                .id(e.getSeverity().getId())
                .levelName(e.getSeverity().getLevelName())
                .slaConfigured(sla)
                .build();
    }

    private IncidentResponse.ReporterResponse toReporterDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        return IncidentResponse.ReporterResponse.builder()
                .id(user.getId())
                .employeeCode(user.getEmployeeCode())
                .displayName(buildUserName(user))
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    private List<IncidentResponse.TimelineResponse> toTimelineDtos(List<IncidentTimelineEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(t -> IncidentResponse.TimelineResponse.builder()
                        .id(t.getId())
                        .action(t.getAction())
                        .reason(null)
                        .actor(
                                IncidentResponse.ActorResponse.builder()
                                        .id(t.getActor().getId())
                                        .displayName(buildUserName(t.getActor()))
                                        .build()
                        )
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();
    }

    private Long calcSlaCountDown(IncidentEntity e) {
        if (e.getSlaDeadline() == null) {
            return null;
        }

        return Duration.between(
                OffsetDateTime.now(),
                e.getSlaDeadline()
        ).toHours();
    }

    private String buildResidentName(ResidentEntity resident) {
        return String.join(" ",
                resident.getFirstName(),
                resident.getMiddleName() == null ? "" : resident.getMiddleName(),
                resident.getLastName()
        ).trim();
    }

    private String buildUserName(UserEntity user) {
        return String.join(" ",
                user.getFirstName() == null ? "" : user.getFirstName(),
                user.getLastName() == null ? "" : user.getLastName()
        ).trim();
    }
}