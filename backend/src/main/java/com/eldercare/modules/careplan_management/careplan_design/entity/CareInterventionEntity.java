package com.eldercare.modules.careplan_management.careplan_design.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "care_interventions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "careGoal")
public class CareInterventionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String name;

    @Column(name = "assigned_role", nullable = false)
    private String assignedRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_goal_id", nullable = false)
    private CareGoalEntity careGoal;

    public CareInterventionEntity(int id, String name, String assignedRole) {
        this.id = (long) id;
        this.name = name;
        this.assignedRole = assignedRole;
    }

    public void setId(int id) {
        this.id = (long) id;
    }
}
