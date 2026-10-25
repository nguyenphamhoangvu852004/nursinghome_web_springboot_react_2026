package com.eldercare.modules.resident_intake.resident.repository;

import com.eldercare.modules.resident_intake.family_contacts.ResidentContactEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentContactRepository extends JpaRepository<ResidentContactEntity, Long> {

  List<ResidentContactEntity> findByResidentId(Long residentId);

  @EntityGraph(attributePaths = { "contact", "contact.address" })
  List<ResidentContactEntity> findAllByResidentIdOrderByIsPrimaryDescCreatedAtAsc(Long residentId);

  boolean existsByResidentIdAndContactId(Long residentId, Long contactId);

  @EntityGraph(attributePaths = { "resident", "contact", "contact.address" })
  Optional<ResidentContactEntity> findByIdAndResidentId(Long id, Long residentId);

  @Query("""
      SELECT COALESCE(SUM(rc.financialResponsibilityPct), 0)
      FROM ResidentContactEntity rc
      WHERE rc.resident.id = :residentId
      """)
  BigDecimal sumFinancialResponsibility(@Param("residentId") Long residentId);

  @Query("""
      SELECT COALESCE(SUM(rc.financialResponsibilityPct), 0)
      FROM ResidentContactEntity rc
      WHERE rc.resident.id = :residentId
        AND rc.id <> :residentContactId
      """)
  BigDecimal sumFinancialResponsibilityExcluding(
      @Param("residentId") Long residentId,
      @Param("residentContactId") Long residentContactId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE ResidentContactEntity rc
      SET rc.isPrimary = false
      WHERE rc.resident.id = :residentId
        AND rc.isPrimary = true
        AND (:excludedId IS NULL OR rc.id <> :excludedId)
      """)
  int clearPrimaryContact(
      @Param("residentId") Long residentId,
      @Param("excludedId") Long excludedId);

  @Query("""
      SELECT rc
      FROM ResidentContactEntity rc
      JOIN FETCH rc.contact c
      LEFT JOIN FETCH c.address
      WHERE rc.resident.id = :residentId
      ORDER BY rc.isPrimary DESC, rc.createdAt ASC
      """)
  List<ResidentContactEntity> findResidentContacts(@Param("residentId") Long residentId);

  List<ResidentContactEntity> findByResident_Id(Long residentId);

  List<ResidentContactEntity> findByContact_Id(Long contactId);

  Optional<ResidentContactEntity> findByIdAndResident_Id(Long id, Long residentId);

  boolean existsByResident_IdAndContact_Id(Long residentId, Long contactId);

  @Query("""
      SELECT rc
      FROM ResidentContactEntity rc
      JOIN FETCH rc.contact c
      WHERE rc.resident.id = :residentId
      ORDER BY rc.isPrimary DESC, rc.id ASC
      """)
  List<ResidentContactEntity> findAllByResidentIdWithContact(@Param("residentId") Long residentId);

  @Query("""
      SELECT rc
      FROM ResidentContactEntity rc
      JOIN FETCH rc.resident r
      WHERE rc.contact.id = :contactId
      ORDER BY rc.id ASC
      """)
  List<ResidentContactEntity> findAllByContactIdWithResident(@Param("contactId") Long contactId);

  @Query("""
      SELECT COALESCE(SUM(rc.financialResponsibilityPct), 0)
      FROM ResidentContactEntity rc
      WHERE rc.resident.id = :residentId
        AND (:excludedId IS NULL OR rc.id <> :excludedId)
      """)
  BigDecimal sumFinancialResponsibility(
      @Param("residentId") Long residentId,
      @Param("excludedId") Long excludedId);

  @Query("""
      SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END
      FROM ResidentContactEntity rc
      WHERE rc.contact.id = :contactId
        AND rc.isGuarantor = true
        AND rc.resident.status = 'ACTIVE'
      """)
  boolean existsAsGuarantorOfActiveResident(@Param("contactId") Long contactId);

  Optional<ResidentContactEntity> findFirstByResident_IdAndIsGuarantorTrueAndContact_IsDeletedFalseOrderByIdAsc(
      Long residentId);

  Optional<ResidentContactEntity> findFirstByResident_IdAndIsPrimaryTrueAndContact_IsDeletedFalseOrderByIdAsc(
      Long residentId);
}