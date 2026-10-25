package com.eldercare.modules.risk_incident.sla_rules.service;

import com.eldercare.modules.risk_incident.sla_rules.dto.CreateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.dto.SLAResponse;
import com.eldercare.modules.risk_incident.sla_rules.dto.UpdateSLARequest;
import java.util.List;

public interface SLAConfigService {

    List<SLAResponse> getAllSLAConfigs();

    SLAResponse createSLAConfig(CreateSLARequest request);

    SLAResponse updateSLAConfig(Long slaConfigId, UpdateSLARequest request);
}
