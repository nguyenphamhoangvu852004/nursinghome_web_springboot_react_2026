package com.eldercare.modules.risk_incident.sla_rules.controller;

import com.eldercare.modules.risk_incident.sla_rules.dto.CreateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.dto.SLAResponse;
import com.eldercare.modules.risk_incident.sla_rules.dto.UpdateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.service.SLAConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sla-configs")
@RequiredArgsConstructor
@Validated
public class SLAConfigController {

    private final SLAConfigService slaConfigService;

    @GetMapping
    public List<SLAResponse> getAllSLAConfigs() {
        return slaConfigService.getAllSLAConfigs();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SLAResponse createSLAConfig(@Valid @RequestBody CreateSLARequest request) {
        return slaConfigService.createSLAConfig(request);
    }

    @PutMapping("/{slaConfigId}")
    public SLAResponse updateSLAConfig(
            @PathVariable Long slaConfigId,
            @Valid @RequestBody UpdateSLARequest request) {
        return slaConfigService.updateSLAConfig(slaConfigId, request);
    }
}
