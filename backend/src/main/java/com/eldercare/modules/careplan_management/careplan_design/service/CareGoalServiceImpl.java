package com.eldercare.modules.careplan_management.careplan_design.service;

import org.springframework.stereotype.Service;

import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CareGoalMapper;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CarePlanMapper;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCarePlanRepository;

import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class CareGoalServiceImpl implements ICareGoalService {

    private final JpaCarePlanRepository carePlanRepository;
    private final JpaCareGoalRepository careGoalRepository;

    public CareGoalServiceImpl(JpaCarePlanRepository carePlanRepository, JpaCareGoalRepository careGoalRepository) {
        this.carePlanRepository = carePlanRepository;
        this.careGoalRepository = careGoalRepository;
    }

    @Override
    @Transactional()
    public AddCareGoalResponseDTO addCareGoal(AddCareGoalRequestDTO requestDTO, int carePlanId) {
        // find care plan
        CarePlanEntity carePlanEntity = this.carePlanRepository.findById((long) carePlanId)
                .map(CarePlanMapper::prepareForRead)
                .orElseThrow(() -> new IllegalArgumentException("Care plan with id " + carePlanId + " not found"));

        CareGoalEntity careGoalEntity = new CareGoalEntity();
        // careGoalEntity.setListCareIntervention();
        careGoalEntity.setDesiredOutCome(requestDTO.careGoalDescription);
        careGoalEntity.setName(requestDTO.careGoalName);
//        careGoalEntity.initState();// set it to in progress

        CareGoalMapper.attachToCarePlan(careGoalEntity, carePlanEntity);
        CareGoalEntity careGoalEntityAfterInserted = this.careGoalRepository.save(careGoalEntity);

        AddCareGoalResponseDTO resposneDto = new AddCareGoalResponseDTO(carePlanEntity.getId().intValue(),
                careGoalEntityAfterInserted.getId().intValue());
        return resposneDto;
    }

    @Override
    public HashMap<String,Object> removeCareGoal(List<Long> carePlanIds) {
            this.careGoalRepository.deleteAllById(carePlanIds);
            return null;
    }

}
