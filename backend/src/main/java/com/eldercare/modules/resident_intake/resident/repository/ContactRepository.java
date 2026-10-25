package com.eldercare.modules.resident_intake.resident.repository;

import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

      @Query("""
                  SELECT c
                  FROM ContactEntity c
                  WHERE (:includeDeleted = true OR c.isDeleted = false)
                    AND (
                        :search IS NULL
                        OR :search = ''
                        OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR c.phonePrimary LIKE CONCAT('%', :search, '%')
                        OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                  """)
      Page<ContactEntity> searchContacts(
                  @Param("search") String search,
                  @Param("includeDeleted") boolean includeDeleted,
                  Pageable pageable);

      boolean existsByEmailIgnoreCase(String email);

      boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

      Optional<ContactEntity> findByIdAndIsDeletedFalse(Long id);
}