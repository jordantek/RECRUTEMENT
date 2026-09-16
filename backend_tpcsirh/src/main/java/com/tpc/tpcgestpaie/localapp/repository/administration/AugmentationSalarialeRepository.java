package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.AugmentationSalariale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AugmentationSalarialeRepository extends JpaRepository<AugmentationSalariale, Long> {

    // === MÉTHODES PAGINÉES ===

    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique")
    Page<AugmentationSalariale> findAllWithAssociations(Pageable pageable);

    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique " +
            "WHERE a.employe.id = :employeId")
    Page<AugmentationSalariale> findByEmployeId(@Param("employeId") Long employeId, Pageable pageable);

    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique " +
            "WHERE a.employe.id = :employeId " +
            "ORDER BY a.dateEffet DESC")
    Page<AugmentationSalariale> findByEmployeIdOrderByDateEffetDesc(@Param("employeId") Long employeId, Pageable pageable);

    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique " +
            "WHERE a.contratEmploye.id = :contratEmployeId " +
            "ORDER BY a.createdAt DESC")
    Page<AugmentationSalariale> findByContratEmployeIdOrderByCreatedAtDesc(@Param("contratEmployeId") Long contratEmployeId, Pageable pageable);


    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique " +
            "WHERE a.company.id = :companyId")
    Page<AugmentationSalariale> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM AugmentationSalariale a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.rubriques r " +
            "LEFT JOIN FETCH r.rubrique " +
            "WHERE a.contratEmploye.id = :contratEmployeId")
    Page<AugmentationSalariale> findByContratEmployeId(@Param("contratEmployeId") Long contratEmployeId, Pageable pageable);

    // === MÉTHODES DE COMPTAGE ===

    @Query("SELECT COUNT(a) FROM AugmentationSalariale a WHERE a.employe.id = :employeId")
    long countByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT COUNT(a) FROM AugmentationSalariale a WHERE a.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM AugmentationSalariale a WHERE a.contratEmploye.id = :contratEmployeId")
    long countByContratEmployeId(@Param("contratEmployeId") Long contratEmployeId);

    // === MÉTHODES DE VÉRIFICATION (non paginées) ===

    boolean existsByEmployeIdAndDateEffetAndMotifAugmentation(Long employeId, LocalDate dateEffet, String motifAugmentation);
}