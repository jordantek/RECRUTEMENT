package com.tpc.tpcgestpaie.localapp.repository.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.StatutJourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JourFerieEntrepriseRepository extends JpaRepository<JourFerieEntreprise, Long> {

    // ========================================
    // RÉCUPÉRATION PAR ENTREPRISE
    // ========================================

    /**
     * Trouve tous les jours fériés d'une entreprise (ADD et REMOVE)
     */
    List<JourFerieEntreprise> findByCompanyIdAndDeletedAtIsNullOrderByDateFerieAsc(Long companyId);

    /**
     * Trouve uniquement les jours fériés AJOUTÉS par l'entreprise
     */
    List<JourFerieEntreprise> findByCompanyIdAndStatutAndDeletedAtIsNullOrderByDateFerieAsc(
            Long companyId, StatutJourFerieEntreprise statut);

    /**
     * Trouve les jours fériés d'une entreprise pour une année
     */
    @Query("SELECT j FROM JourFerieEntreprise j WHERE j.company.id = :companyId " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerieEntreprise> findByCompanyIdAndAnnee(
            @Param("companyId") Long companyId,
            @Param("annee") int annee);

    /**
     * Vérifie si une entreprise a retiré un jour férié national
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END " +
            "FROM JourFerieEntreprise j " +
            "WHERE j.company.id = :companyId " +
            "AND j.slug = :slug " +
            "AND j.statut = 'REMOVE' " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    boolean hasRemovedJourFerie(
            @Param("companyId") Long companyId,
            @Param("slug") String slug,
            @Param("annee") int annee);

    /**
     * Vérifie si un slug existe déjà pour une entreprise et une année
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END " +
            "FROM JourFerieEntreprise j " +
            "WHERE j.company.id = :companyId " +
            "AND j.slug = :slug " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    boolean existsByCompanyIdAndSlugAndAnnee(
            @Param("companyId") Long companyId,
            @Param("slug") String slug,
            @Param("annee") int annee);

    /**
     * Vérifie si une date est un jour férié pour une entreprise
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END " +
            "FROM JourFerieEntreprise j " +
            "WHERE j.company.id = :companyId " +
            "AND j.dateFerie = :date " +
            "AND j.statut = 'ADD' " +
            "AND j.deletedAt IS NULL")
    boolean isJourFerieEntreprise(
            @Param("companyId") Long companyId,
            @Param("date") LocalDate date);

    /**
     * Compte le nombre de jours fériés personnalisés d'une entreprise
     */
    @Query("SELECT COUNT(j) FROM JourFerieEntreprise j " +
            "WHERE j.company.id = :companyId " +
            "AND j.statut = 'ADD' " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    long countJoursFeriesPersonnalises(
            @Param("companyId") Long companyId,
            @Param("annee") int annee);

    /**
     * Compte le nombre de jours fériés retirés
     */
    @Query("SELECT COUNT(j) FROM JourFerieEntreprise j " +
            "WHERE j.company.id = :companyId " +
            "AND j.statut = 'REMOVE' " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    long countJoursFeriesRetires(
            @Param("companyId") Long companyId,
            @Param("annee") int annee);

    // ========================================
    // SUPPRESSION
    // ========================================

    /**
     * Supprime tous les jours fériés d'une entreprise pour une année
     */
    @Query("UPDATE JourFerieEntreprise j SET j.deletedAt = CURRENT_TIMESTAMP " +
            "WHERE j.company.id = :companyId AND YEAR(j.dateFerie) = :annee")
    void deleteAllByCompanyIdAndAnnee(
            @Param("companyId") Long companyId,
            @Param("annee") int annee);
}