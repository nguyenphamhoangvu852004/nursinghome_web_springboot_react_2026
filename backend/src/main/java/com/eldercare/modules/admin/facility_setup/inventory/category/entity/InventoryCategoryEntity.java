package com.eldercare.modules.admin.facility_setup.inventory.category.entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.entity.ConsumableSupplyEntity;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.entity.DurableMedicalEquipmentEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_categories")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE inventory_categories SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Data
public class InventoryCategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Column(name = "category_name", nullable = false, unique = true, length = 100)
  private String categoryName;

  @Column(length = 255)
  private String description;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  private boolean isDeleted = false;

  @OneToMany(mappedBy = "category")
  private List<ConsumableSupplyEntity> consumableSupplies;

  @OneToMany(mappedBy = "category")
  private List<DurableMedicalEquipmentEntity> durableMedicalEquipments;

}
