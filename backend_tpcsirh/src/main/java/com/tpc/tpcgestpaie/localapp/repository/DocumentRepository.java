package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // =============================
    // Recherche simple
    // =============================

    Optional<Document> findByReference(String reference);

    boolean existsByReference(String reference);


    // =============================
    // Entreprise
    // =============================

    List<Document> findByCompanyId(Long companyId);

    List<Document> findByCompanyIdAndDeletedAtIsNull(Long companyId);


    // =============================
    // Employé
    // =============================

    List<Document> findByEmployeeId(Long employeeId);

    List<Document> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

    List<Document> findByCompanyIdAndEmployeeIdAndDeletedAtIsNull(Long companyId, Long employeeId);


    // =============================
    // Catégorie
    // =============================

    List<Document> findByCategory_Id(Long categoryId);

    List<Document> findByCompanyIdAndCategory_Id(Long companyId, Long categoryId);


    // =============================
    // Modèle de document
    // =============================

    List<Document> findByModel_Id(Long modelId);

    List<Document> findByCompanyIdAndModel_Id(Long companyId, Long modelId);


    // =============================
    // Documents scannés
    // =============================

    List<Document> findByIsScannedTrue();

    List<Document> findByCompanyIdAndIsScannedTrue(Long companyId);


    // =============================
    // Documents actifs (non supprimés)
    // =============================

    List<Document> findByDeletedAtIsNull();

    List<Document> findByCompanyIdAndDeletedAtIsNullAndEmployeeId(Long companyId, Long employeeId);


    // =============================
    // Documents expirés
    // =============================

    List<Document> findByValidToBefore(java.time.LocalDate date);

    List<Document> findByCompanyIdAndValidToBefore(Long companyId, java.time.LocalDate date);

    //
    @Query("SELECT d FROM Document d " +
            "WHERE d.employeeId = :employeeId " +
            "AND d.signatureDate IS NOT NULL " +
            "AND d.showOnEmployeeProfile = true " +
            "ORDER BY d.signatureDate DESC")
    List<Document> findSignedDocumentsForEmployeeProfileByEmployee(
            @Param("employeeId") Long employeeId
    );
}