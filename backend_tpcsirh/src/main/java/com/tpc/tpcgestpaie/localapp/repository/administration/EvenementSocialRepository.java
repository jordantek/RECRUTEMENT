package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.EvenementSocial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EvenementSocialRepository extends JpaRepository<EvenementSocial, Long> {

    // === MÉTHODES PAGINÉES ===

    @Query("SELECT e FROM EvenementSocial e " +
            "LEFT JOIN FETCH e.employe " +
            "LEFT JOIN FETCH e.contratEmploye " +
            "LEFT JOIN FETCH e.company")
    Page<EvenementSocial> findAllWithAssociations(Pageable pageable);

    @Query("SELECT e FROM EvenementSocial e " +
            "LEFT JOIN FETCH e.employe " +
            "LEFT JOIN FETCH e.contratEmploye " +
            "LEFT JOIN FETCH e.company " +
            "WHERE e.company.id = :companyId")
    Page<EvenementSocial> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT e FROM EvenementSocial e " +
            "LEFT JOIN FETCH e.employe " +
            "LEFT JOIN FETCH e.contratEmploye " +
            "LEFT JOIN FETCH e.company " +
            "WHERE e.employe.id = :employeId")
    Page<EvenementSocial> findByEmployeId(@Param("employeId") Long employeId, Pageable pageable);

    @Query("SELECT e FROM EvenementSocial e " +
            "LEFT JOIN FETCH e.employe " +
            "LEFT JOIN FETCH e.contratEmploye " +
            "LEFT JOIN FETCH e.company " +
            "WHERE e.employe.id = :employeId " +
            "AND e.dateEvenement = :dateEvenement " +
            "AND e.designation = :designation")
    Page<EvenementSocial> findByEmployeIdAndDateEvenementAndDesignation(
            @Param("employeId") Long employeId,
            @Param("dateEvenement") LocalDate dateEvenement,
            @Param("designation") String designation,
            Pageable pageable);

    // Méthodes de comptage pour la pagination
    @Query("SELECT COUNT(e) FROM EvenementSocial e WHERE e.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(e) FROM EvenementSocial e WHERE e.employe.id = :employeId")
    long countByEmployeId(@Param("employeId") Long employeId);
}