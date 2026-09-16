package com.tpc.tpcgestpaie.localapp.repository.absence;

import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeAbsenceRepository extends JpaRepository<DemandeAbsence, Long>,
        JpaSpecificationExecutor<DemandeAbsence> {

    // ========================================
    // VÉRIFICATIONS DE DOUBLONS ET CHEVAUCHEMENTS
    // ========================================

    /**
     * Vérifier si une demande existe déjà pour un employé avec les mêmes dates
     */
    boolean existsByEmployeDemandeurAndDateDebutAndDateFin(
            Employe employe, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Vérifier les chevauchements de périodes
     * Exclut les demandes rejetées et annulées
     */
    @Query("SELECT COUNT(d) > 0 FROM DemandeAbsence d WHERE " +
            "d.employeDemandeur = :employe AND " +
            "d.statut NOT IN ('REJETE', 'ANNULE') AND " +
            "((d.dateDebut BETWEEN :dateDebut AND :dateFin) OR " +
            "(d.dateFin BETWEEN :dateDebut AND :dateFin) OR " +
            "(d.dateDebut <= :dateDebut AND d.dateFin >= :dateFin))")
    boolean existsByEmployeDemandeurAndDateDebutBetweenOrDateFinBetween(
            @Param("employe") Employe employe,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    // ========================================
    // RECHERCHE PAR EMPLOYÉ
    // ========================================

    /**
     * Historique complet d'un employé (toutes les demandes)
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByEmployeDemandeurIdOrderByCreatedAtDesc(@Param("employeId") Long employeId);

    /**
     * Liste simplifiée ordonnée par date de début
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findByEmployeDemandeurIdOrderByDateDebutDesc(@Param("employeId") Long employeId);

    /**
     * Demandes d'un employé avec un statut spécifique
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId AND d.statut = :statut ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findByEmployeDemandeurIdAndStatutOrderByDateDebutDesc(
            @Param("employeId") Long employeId,
            @Param("statut") StatutDemandeAbsence statut);

    /**
     * Demandes récentes (après une date donnée)
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId AND d.dateDebut > :date ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findByEmployeDemandeurIdAndDateDebutAfterOrderByDateDebutDesc(
            @Param("employeId") Long employeId,
            @Param("date") LocalDate date);

    /**
     * Pagination pour un employé
     */
    Page<DemandeAbsence> findByEmployeDemandeurId(Long employeId, Pageable pageable);

    // ========================================
    // COMPTAGES PAR EMPLOYÉ
    // ========================================

    /**
     * Total des demandes d'un employé
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId")
    long countByEmployeDemandeurId(@Param("employeId") Long employeId);

    /**
     * Comptage par employé et statut
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId AND d.statut = :statut")
    long countByEmployeDemandeurIdAndStatut(
            @Param("employeId") Long employeId,
            @Param("statut") StatutDemandeAbsence statut);

    // ========================================
    // VALIDATIONS - RECHERCHE PAR NIVEAU (NOUVEAU WORKFLOW)
    // ========================================

    /**
     * Demandes en attente de validation pour un validateur spécifique
     * Utilise le JSON hierarchie_validation
     */
    @Query(value = """
        SELECT d.* FROM demande_absences d
        WHERE d.niveau_validation_en_cours IS NOT NULL
        AND JSON_EXTRACT(d.hierarchie_validation, 
            CONCAT('$.niveaux[', d.niveau_validation_en_cours - 1, '].employe_superieur_id')
        ) = :validateurId
        AND JSON_EXTRACT(d.hierarchie_validation, 
            CONCAT('$.niveaux[', d.niveau_validation_en_cours - 1, '].statut')
        ) = 'EN_ATTENTE'
        ORDER BY d.created_at DESC
    """, nativeQuery = true)
    List<DemandeAbsence> findDemandesEnAttenteValidationPour(@Param("validateurId") Long validateurId);

    /**
     * Toutes les demandes où un employé est validateur (tous niveaux)
     */
    @Query(value = """
        SELECT DISTINCT d.* FROM demande_absences d
        WHERE JSON_SEARCH(
            d.hierarchie_validation,
            'one',
            CAST(:validateurId AS CHAR),
            NULL,
            '$.niveaux[*].employe_superieur_id'
        ) IS NOT NULL
        ORDER BY d.created_at DESC
    """, nativeQuery = true)
    List<DemandeAbsence> findDemandesOuValidateur(@Param("validateurId") Long validateurId);

    /**
     * Demandes validées par un validateur
     */
    @Query(value = """
        SELECT DISTINCT d.* FROM demande_absences d
        WHERE JSON_SEARCH(
            d.hierarchie_validation,
            'one',
            CAST(:validateurId AS CHAR),
            NULL,
            '$.niveaux[*].employe_superieur_id'
        ) IS NOT NULL
        AND EXISTS (
            SELECT 1 FROM validation_niveaux vn
            WHERE vn.demande_absence_id = d.id
            AND vn.validateur_id = :validateurId
            AND vn.statut = 'APPROUVE'
        )
        ORDER BY d.created_at DESC
    """, nativeQuery = true)
    List<DemandeAbsence> findDemandesValideesPar(@Param("validateurId") Long validateurId);

    /**
     * Demandes rejetées par un validateur
     */
    @Query(value = """
        SELECT DISTINCT d.* FROM demande_absences d
        WHERE JSON_SEARCH(
            d.hierarchie_validation,
            'one',
            CAST(:validateurId AS CHAR),
            NULL,
            '$.niveaux[*].employe_superieur_id'
        ) IS NOT NULL
        AND EXISTS (
            SELECT 1 FROM validation_niveaux vn
            WHERE vn.demande_absence_id = d.id
            AND vn.validateur_id = :validateurId
            AND vn.statut = 'REJETE'
        )
        ORDER BY d.created_at DESC
    """, nativeQuery = true)
    List<DemandeAbsence> findDemandesRejeteesPar(@Param("validateurId") Long validateurId);

    // ========================================
    // RECHERCHE PAR ENTREPRISE
    // ========================================

    /**
     * Toutes les demandes d'une entreprise
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.companyId = :companyId ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * Demandes d'une entreprise par statut
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.companyId = :companyId AND d.statut = :statut ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByCompanyIdAndStatut(
            @Param("companyId") Long companyId,
            @Param("statut") StatutDemandeAbsence statut);

    /**
     * Demandes pour plusieurs entreprises
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.company IN :companies ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByCompanies(@Param("companies") List<Company> companies);

    /**
     * Pagination pour plusieurs entreprises
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.company IN :companies ORDER BY d.createdAt DESC")
    Page<DemandeAbsence> findByCompanies(@Param("companies") List<Company> companies, Pageable pageable);

    /**
     * Comptage par entreprise
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.companyId = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    /**
     * Comptage par entreprise et statut
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.companyId = :companyId AND d.statut = :statut")
    long countByCompanyIdAndStatut(
            @Param("companyId") Long companyId,
            @Param("statut") StatutDemandeAbsence statut);

    /**
     * Comptage pour plusieurs entreprises
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.companyId IN :companyIds")
    long countByCompanies(@Param("companyIds") List<Long> companyIds);

    /**
     * Comptage par entreprises et statut
     */
    @Query("SELECT COUNT(d) FROM DemandeAbsence d WHERE d.companyId IN :companyIds AND d.statut = :statut")
    long countByCompaniesAndStatut(
            @Param("companyIds") List<Long> companyIds,
            @Param("statut") StatutDemandeAbsence statut);

    // ========================================
    // RECHERCHE PAR STATUT
    // ========================================

    /**
     * Toutes les demandes par statut
     */
    List<DemandeAbsence> findByStatutOrderByCreatedAtDesc(StatutDemandeAbsence statut);

    /**
     * Demandes en attente de validation (tous niveaux)
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.statut IN ('EN_ATTENTE_VALIDATION', 'EN_COURS_VALIDATION') ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findAllEnAttenteValidation();

    /**
     * Demandes approuvées définitivement
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.statut = 'APPROUVE_FINAL' ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findAllApprouvees();

    /**
     * Demandes en cours (départ confirmé, pas encore de retour)
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.statut = 'EN_COURS' ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findAllEnCours();

    /**
     * Demandes terminées
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.statut = 'TERMINEE' ORDER BY d.dateFinEffective DESC")
    List<DemandeAbsence> findAllTerminees();

    // ========================================
    // RECHERCHE PAR DATES
    // ========================================

    /**
     * Demandes dont le départ est prévu à une date donnée
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.dateDebut = :date AND d.statut = 'APPROUVE_FINAL'")
    List<DemandeAbsence> findByDateDebutPrevue(@Param("date") LocalDate date);

    /**
     * Demandes dont le retour est prévu à une date donnée
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.dateFin = :date AND d.statut IN ('APPROUVE_FINAL', 'EN_COURS')")
    List<DemandeAbsence> findByDateRetourPrevue(@Param("date") LocalDate date);

    /**
     * Demandes sur une période donnée
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE " +
            "d.dateDebut BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY d.dateDebut ASC")
    List<DemandeAbsence> findByPeriode(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Demandes d'une année donnée
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE YEAR(d.dateDebut) = :annee ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findByAnnee(@Param("annee") int annee);

    /**
     * Demandes d'un mois donné
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE YEAR(d.dateDebut) = :annee AND MONTH(d.dateDebut) = :mois ORDER BY d.dateDebut DESC")
    List<DemandeAbsence> findByMois(@Param("annee") int annee, @Param("mois") int mois);

    // ========================================
    // RECHERCHE PAR TYPE/MOTIF
    // ========================================

    /**
     * Demandes par type d'absence
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.typeAbsence.id = :typeAbsenceId ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByTypeAbsenceId(@Param("typeAbsenceId") Long typeAbsenceId);


    // ========================================
    // STATISTIQUES ET RAPPORTS
    // ========================================

    /**
     * Nombre total de jours d'absence prévus pour un employé (approuvées uniquement)
     */
    @Query("SELECT COALESCE(SUM(d.nombreJours), 0) FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "AND d.statut = 'APPROUVE_FINAL'")
    long sumJoursPrevusByEmploye(@Param("employeId") Long employeId);

    /**
     * Nombre total de jours effectivement pris
     */
    @Query("SELECT COALESCE(SUM(d.nombreJoursEffectifs), 0) FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "AND d.statut = 'TERMINEE'")
    long sumJoursEffectifsByEmploye(@Param("employeId") Long employeId);

    /**
     * Statistiques par entreprise et année
     */
    @Query("""
        SELECT 
            d.statut as statut,
            COUNT(d) as nombre,
            COALESCE(SUM(d.nombreJours), 0) as totalJours
        FROM DemandeAbsence d
        WHERE d.companyId = :companyId
        AND YEAR(d.dateDebut) = :annee
        GROUP BY d.statut
    """)
    List<Object[]> getStatistiquesByCompanyAndYear(
            @Param("companyId") Long companyId,
            @Param("annee") int annee);

    // ========================================
    // RECHERCHE AVANCÉE AVEC SPECIFICATIONS
    // ========================================

    /**
     * Recherche avec critères multiples (utilise JpaSpecificationExecutor)
     */
    Page<DemandeAbsence> findAll(Specification<DemandeAbsence> spec, Pageable pageable);

    // ========================================
    // REPORTS
    // ========================================

    /**
     * Demandes reportées
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.estReporte = true ORDER BY d.dateReport DESC")
    List<DemandeAbsence> findAllReportees();

    /**
     * Demandes reportées par employé
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId AND d.estReporte = true ORDER BY d.dateReport DESC")
    List<DemandeAbsence> findReporteesByEmploye(@Param("employeId") Long employeId);

    // ========================================
    // DEMANDES À TRAITER (NOTIFICATIONS)
    // ========================================

    /**
     * Demandes approuvées dont le départ est dans X jours (pour notification J-X)
     */
    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.statut = 'APPROUVE_FINAL' " +
            "AND d.dateDebut = :date " +
            "AND NOT EXISTS (SELECT 1 FROM SuiviAbsence s WHERE s.demandeAbsence.id = d.id AND s.departConfirme = true)")
    List<DemandeAbsence> findDepartsANotifier(@Param("date") LocalDate date);

    /**
     * Demandes dont le retour est prévu pour notification
     */
    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.statut = 'EN_COURS' " +
            "AND d.dateFin = :date " +
            "AND NOT EXISTS (SELECT 1 FROM SuiviAbsence s WHERE s.demandeAbsence.id = d.id AND s.retourConfirme = true)")
    List<DemandeAbsence> findRetoursANotifier(@Param("date") LocalDate date);

    // ========================================
    // UTILITAIRES
    // ========================================

    /**
     * Dernière demande d'un employé
     */
    @Query("SELECT d FROM DemandeAbsence d WHERE d.employeDemandeur.id = :employeId ORDER BY d.createdAt DESC LIMIT 1")
    Optional<DemandeAbsence> findLastByEmploye(@Param("employeId") Long employeId);

    /**
     * Vérifier si un employé a des absences en cours
     */
    @Query("SELECT COUNT(d) > 0 FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "AND d.statut IN ('APPROUVE_FINAL', 'EN_COURS') " +
            "AND d.dateDebut <= CURRENT_DATE " +
            "AND d.dateFin >= CURRENT_DATE")
    boolean hasAbsenceEnCours(@Param("employeId") Long employeId);

    /**
     * Demandes en conflit avec une période donnée
     */
    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "AND d.statut NOT IN ('REJETE', 'ANNULE') " +
            "AND ((d.dateDebut BETWEEN :dateDebut AND :dateFin) " +
            "OR (d.dateFin BETWEEN :dateDebut AND :dateFin) " +
            "OR (d.dateDebut <= :dateDebut AND d.dateFin >= :dateFin))")
    List<DemandeAbsence> findConflits(
            @Param("employeId") Long employeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);


    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByEmployeDemandeurIdOrderByDateCreationDesc(@Param("employeId") Long employeId);



    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.company.id = :companyId " +
            "ORDER BY d.createdAt DESC")
    List<DemandeAbsence> findByCompanyIdOrderByDateCreationDesc(@Param("companyId") Long companyId);

    List<DemandeAbsence> findByEmployeDemandeurIdAndStatutAndDateDebutAfter(
            Long employeId,
            StatutDemandeAbsence statut,
            LocalDate dateDebut
    );




    @Query("SELECT d FROM DemandeAbsence d " +
            "WHERE d.employeDemandeur.id = :employeId " +
            "AND d.dateDebut BETWEEN :debut AND :fin")
    List<DemandeAbsence> findByEmployeAndPeriode(
            @Param("employeId") Long employeId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}