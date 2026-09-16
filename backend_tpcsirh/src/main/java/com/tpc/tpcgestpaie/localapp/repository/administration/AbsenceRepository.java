package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.Absence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // === MÉTHODES PAGINÉES ===
    List<Absence> findByEmployeIdAndConditionAcceptation(Long employeId, String conditionAcceptation);

    @EntityGraph(attributePaths = {
            "employe",
            "contratEmploye",
            "company",
            "typeAbsence",
            "creditConge",
            "added_by"
    })
    Page<Absence> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "employe",
            "contratEmploye",
            "company",
            "typeAbsence",
            "creditConge",
            "added_by"
    })
    Page<Absence> findByCompanyId(Long companyId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "contratEmploye",
            "company",
            "typeAbsence",
            "creditConge",
            "added_by"
    })
    Page<Absence> findByEmployeId(Long employeId, Pageable pageable);

    // === MÉTHODES AVEC FILTRES PAGINÉES ===

    @Query("SELECT a FROM Absence a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.typeAbsence " +
            "LEFT JOIN FETCH a.creditConge " +
            "LEFT JOIN FETCH a.added_by " +
            "WHERE (:typeId IS NULL OR a.typeAbsence.id = :typeId) AND " +
            "(:dateDebut IS NULL OR a.dateDebut >= :dateDebut) AND " +
            "(:dateFin IS NULL OR a.dateFin <= :dateFin)")
    Page<Absence> filterByMotifOrPeriode(@Param("typeId") Long typeId,
                                         @Param("dateDebut") LocalDate dateDebut,
                                         @Param("dateFin") LocalDate dateFin,
                                         Pageable pageable);

    // === MÉTHODES SPÉCIFIQUES PAGINÉES ===

    @Query("SELECT a FROM Absence a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.typeAbsence " +
            "WHERE a.contratEmploye.id = :idContratEmploye " +
            "AND a.deleted_at IS NULL " +
            "AND a.conditionAcceptation = 'A_DEDUIRE_DES_CONGES'")
    Page<Absence> findAbsencesValidesByContratEmployeId(@Param("idContratEmploye") Long idContratEmploye, Pageable pageable);

    @Query("SELECT a FROM Absence a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.typeAbsence " +
            "WHERE a.employe.id = :employeId " +
            "AND a.deleted_at IS NULL " +
            "AND a.conditionAcceptation = 'A_DEDUIRE_DES_CONGES' " +
            "AND FUNCTION('MONTH', a.dateDebut) = :mois " +
            "AND FUNCTION('YEAR', a.dateDebut) = :annee")
    Page<Absence> findAbsencesDeduitesDuMois(@Param("employeId") Long employeId,
                                             @Param("mois") int mois,
                                             @Param("annee") int annee,
                                             Pageable pageable);

    @Query("SELECT a FROM Absence a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "LEFT JOIN FETCH a.typeAbsence " +
            "WHERE a.employe.id = :employeId " +
            "AND a.conditionAcceptation = :conditionAcceptation " +
            "AND (a.dateDebut <= :fin AND a.dateFin >= :debut)")
    Page<Absence> findByEmployeIdAndConditionAcceptationAndDateIntersects(

            @Param("employeId") Long employeId,
            @Param("conditionAcceptation") String conditionAcceptation,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            Pageable pageable);

    // === MÉTHODES DE COMPTAGE ===

    long countByCompanyId(Long companyId);
    long countByEmployeId(Long employeId);

    @Query("SELECT COUNT(a) FROM Absence a WHERE " +
            "(:typeId IS NULL OR a.typeAbsence.id = :typeId) AND " +
            "(:dateDebut IS NULL OR a.dateDebut >= :dateDebut) AND " +
            "(:dateFin IS NULL OR a.dateFin <= :dateFin)")
    long countByMotifOrPeriode(@Param("typeId") Long typeId,
                               @Param("dateDebut") LocalDate dateDebut,
                               @Param("dateFin") LocalDate dateFin);

    // === MÉTHODES DE VÉRIFICATION (non paginées) ===

    boolean existsByContratEmployeIdAndDateDebutAndDateFin(Long contratId, LocalDate debut, LocalDate fin);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Absence a
        WHERE a.contratEmploye.id = :contratId
          AND a.dateDebut <= :dateFin
          AND a.dateFin >= :dateDebut
    """)
    boolean existsAbsenceOverlap(@Param("contratId") Long contratId,
                                 @Param("dateDebut") LocalDate dateDebut,
                                 @Param("dateFin") LocalDate dateFin);

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Absence a
    WHERE a.contratEmploye.id = :contratEmployeId
      AND a.id <> :absenceId
      AND (a.dateDebut <= :dateFin AND a.dateFin >= :dateDebut)
    """)
    boolean existsAbsenceOverlapForUpdate(
            @Param("contratEmployeId") Long contratEmployeId,
            @Param("absenceId") Long absenceId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

    // === MÉTHODES DE STATISTIQUES ===

    @Query("SELECT a.typeAbsence, COUNT(a) " +
            "FROM Absence a " +
            "WHERE a.dateDebut <= CURRENT_DATE " +
            "AND a.dateFin >= CURRENT_DATE " +
            "AND a.company.id = :companyId " +
            "GROUP BY a.typeAbsence")
    List<Object[]> countAbsencesEnCoursParType(Long companyId);

    @Query("SELECT " +
            "CASE WHEN a.conditionAcceptation = 'A_DEDUIRE_DES_CONGES' THEN 'CONGE' ELSE 'ABSENCE' END, " +
            "COUNT(a) " +
            "FROM Absence a " +
            "WHERE a.company.id = :companyId " +
            "GROUP BY CASE WHEN a.conditionAcceptation = 'A_DEDUIRE_DES_CONGES' THEN 'CONGE' ELSE 'ABSENCE' END")
    List<Object[]> countAbsencesEtConges(Long companyId);

    @Query("SELECT COALESCE(SUM(CAST(a.duree AS double)),0) FROM Absence a WHERE a.company.id = :companyId")
    long sumDureeAbsencesByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COALESCE(SUM(CAST(a.duree AS double)), 0) " +
            "FROM Absence a " +
            "WHERE a.company.id = :companyId " +
            "AND a.dateFin >= CURRENT_DATE " +
            "AND a.contratEmploye.status_contrat = 'ACTIF'")
    Double sumDureeAbsencesActifs(@Param("companyId") Long companyId);


//GESTION DES ALERTES

    @Query("SELECT a FROM Absence a WHERE a.company.id IN :companyIds AND a.deleted_at IS NULL")
    List<Absence> findByCompanyIdsAndActives(@Param("companyIds") List<Long> companyIds);

    @Query("SELECT a FROM Absence a WHERE a.deleted_at IS NULL")
    List<Absence> findAbsencesActives();

    @Query("SELECT a FROM Absence a WHERE a.employe.id = :employeId AND a.deleted_at IS NULL")
    List<Absence> findAbsencesActivesByEmploye(@Param("employeId") Long employeId);

    @Query("SELECT a FROM Absence a WHERE a.company.id IN :companyIds AND a.deleted_at IS NULL " +
            "AND ((a.dateDebut <= :date AND a.dateFin >= :date) OR a.dateDebut = :date OR a.dateFin = :date)")
    List<Absence> findAbsencesByDate(@Param("companyIds") List<Long> companyIds, @Param("date") LocalDate date);

    @Query("SELECT a FROM Absence a WHERE a.company.id IN :companyIds AND a.deleted_at IS NULL " +
            "AND a.dateDebut BETWEEN :startDate AND :endDate")
    List<Absence> findDepartsProchains(@Param("companyIds") List<Long> companyIds,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Absence a WHERE a.company.id IN :companyIds AND a.deleted_at IS NULL " +
            "AND a.dateFin BETWEEN :startDate AND :endDate")
    List<Absence> findRetoursProchains(@Param("companyIds") List<Long> companyIds,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}