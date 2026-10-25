package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.enums.ConsumableSupplyEnum;

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
@Table(name = "consumable_supplies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE consumable_supplies SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Data
public class ConsumableSupplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private InventoryCategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private FacilityEntity facility;

    @Column(name = "stock_on_hand", nullable = false)
    @Builder.Default
    private int stockOnHand = 0;

    @Column(nullable = false)
    @Builder.Default
    private int total = 0;

    @Column(name = "reorder_threshold", nullable = false)
    @Builder.Default
    private int reorderThreshold = 0;

    @Column(name = "unit_cost", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "private_pay_rate", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal privatePayRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ConsumableSupplyEnum status = ConsumableSupplyEnum.OK;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}
