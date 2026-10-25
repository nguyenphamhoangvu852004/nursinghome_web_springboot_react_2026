package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.enums.DurableMedicalEquipmentEnum;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "durable_medical_equipment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE durable_medical_equipment SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Data
public class DurableMedicalEquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private InventoryCategoryEntity category;

    @Column(name = "asset_tag", length = 50)
    private String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private DurableMedicalEquipmentEnum status = DurableMedicalEquipmentEnum.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private FacilityEntity facility;

    @ManyToOne
    @JoinColumn(name = "assigned_to_user", referencedColumnName = "id")
    private UserEntity assignedToUser;

    @ManyToOne
    @JoinColumn(name = "assigned_to_resident", referencedColumnName = "id")
    private ResidentEntity assignedToResident;

    @Column(name = "unit_value", precision = 18, scale = 2)
    private BigDecimal unitValue;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
