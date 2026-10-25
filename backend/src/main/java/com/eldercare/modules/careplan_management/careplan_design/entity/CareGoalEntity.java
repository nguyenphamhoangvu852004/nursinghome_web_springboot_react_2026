package com.eldercare.modules.careplan_management.careplan_design.entity;

import com.eldercare.common.enums.CarePlanGoalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "care_goals")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "carePlan")
public class CareGoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String name;

    @Column(name = "desired_out_come")
    private String desiredOutCome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_plan_id", nullable = false)
    private CarePlanEntity carePlan;

    @OneToMany(mappedBy = "careGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareInterventionEntity> listCareIntervention = new ArrayList<>();

    public CareGoalEntity(int id, String name, String desiredOutCome,
                          List<CareInterventionEntity> listCareIntervention) {
        this.id = (long) id;
        this.name = name;
        this.desiredOutCome = desiredOutCome;
        this.listCareIntervention = listCareIntervention;
    }

    public void setId(int id) {
        this.id = (long) id;
    }

    public void addIntervention(CareInterventionEntity careInterventionEntity) {
        careInterventionEntity.setCareGoal(this);
        this.listCareIntervention.add(careInterventionEntity);
    }

    public void removeIntervention(int id) {
        for (int i = 0; i < this.listCareIntervention.size(); i++) {
            if (this.listCareIntervention.get(i).getId() == id) {
                this.listCareIntervention.remove(i);
                return;
            }
        }
    }

}
