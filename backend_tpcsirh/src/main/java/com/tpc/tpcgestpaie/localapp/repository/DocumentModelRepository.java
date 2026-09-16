package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.DocumentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentModelRepository extends JpaRepository<DocumentModel, Long> {

    Optional<DocumentModel> findByCodeAndCompanyId(String code, Long companyId);

    boolean existsByCodeAndCompanyId(String code, Long companyId);

    boolean existsByCode(String code);

    List<DocumentModel> findByCompanyId(Long companyId);

    List<DocumentModel> findByCompanyIdIsNull();

    // documents globaux + documents de l'entreprise
    List<DocumentModel> findByCompanyIdOrCompanyIdIsNull(Long companyId);

    // recherche par catégorie
    List<DocumentModel> findByCategoryId(Long categoryId);

    // documents actifs seulement
    List<DocumentModel> findByIsActiveTrue();

    // documents visibles sur la fiche employé
    List<DocumentModel> findByShowOnEmployeeProfileTrue();

    // documents visibles pour une entreprise
    List<DocumentModel> findByCompanyIdOrCompanyIdIsNullAndShowOnEmployeeProfileTrue(Long companyId);

}