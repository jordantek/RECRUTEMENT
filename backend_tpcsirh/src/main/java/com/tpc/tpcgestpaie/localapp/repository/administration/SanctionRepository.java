package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.Sanction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {

    // === MÉTHODES PAGINÉES ===

    @Query("SELECT s FROM Sanction s LEFT JOIN FETCH s.employe LEFT JOIN FETCH s.contratEmploye LEFT JOIN FETCH s.company")
    Page<Sanction> findAllWithAssociations(Pageable pageable);

    @Query("SELECT s FROM Sanction s " +
            "LEFT JOIN FETCH s.employe " +
            "LEFT JOIN FETCH s.contratEmploye " +
            "LEFT JOIN FETCH s.company " +
            "WHERE s.company.id = :companyId")
    Page<Sanction> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT s FROM Sanction s " +
            "LEFT JOIN FETCH s.employe " +
            "LEFT JOIN FETCH s.contratEmploye " +
            "LEFT JOIN FETCH s.company " +
            "WHERE s.employe.id = :employeId")
    Page<Sanction> findByEmployeId(@Param("employeId") Long employeId, Pageable pageable);

    // Méthodes de comptage pour la pagination
    @Query("SELECT COUNT(s) FROM Sanction s WHERE s.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(s) FROM Sanction s WHERE s.employe.id = :employeId")
    long countByEmployeId(@Param("employeId") Long employeId);

    // Recherche avec filtres paginée
    @Query("SELECT s FROM Sanction s " +
            "LEFT JOIN FETCH s.employe " +
            "LEFT JOIN FETCH s.contratEmploye " +
            "LEFT JOIN FETCH s.company " +
            "WHERE s.employe.id = :employeId AND s.datePlainte = :datePlainte AND s.contenuePlainte LIKE %:contenuePlainte%")
    Page<Sanction> findByEmployeIdAndDatePlainteAndContenuePlainte(
            @Param("employeId") Long employeId,
            @Param("datePlainte") LocalDate datePlainte,
            @Param("contenuePlainte") String contenuePlainte,
            Pageable pageable);
}