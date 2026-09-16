package com.tpc.tpcgestpaie.localapp.repository.jourFerie;

import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité JourFerie
 */
@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, Long> {

    // ========================================
    // 🌍 MÉTHODES AVEC PAYS
    // ========================================

    /**
     * Trouve tous les jours fériés d'un pays
     */
    List<JourFerie> findByPaysAndDeletedAtIsNullOrderByDateFerieAsc(String pays);

    /**
     * Trouve les jours fériés d'un pays pour une année spécifique
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays = :codepays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerie> findByPaysAndAnnee(@Param("codepays") String codepays, @Param("annee") int annee);

    /**
     * Trouve les jours fériés récurrents d'un pays
     */
    List<JourFerie> findByPaysAndEstRecurrentTrueAndDeletedAtIsNullOrderByDateFerieAsc(String pays);

    /**
     * Trouve les jours fériés fixes d'un pays
     */
    List<JourFerie> findByPaysAndEstFixeTrueAndDeletedAtIsNullOrderByDateFerieAsc(String pays);

    /**
     * Trouve les jours fériés mobiles d'un pays
     */
    List<JourFerie> findByPaysAndEstFixeFalseAndDeletedAtIsNullOrderByDateFerieAsc(String pays);

    /**
     * Trouve un jour férié par date exacte et pays
     */
    Optional<JourFerie> findByPaysAndDateFerieAndDeletedAtIsNull(String pays, LocalDate dateFerie);

    /**
     * Trouve les jours fériés d'un pays entre deux dates
     */
    List<JourFerie> findByPaysAndDateFerieBetweenAndDeletedAtIsNullOrderByDateFerieAsc(
            String pays, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Vérifie si une date est un jour férié dans un pays
     */
    boolean existsByPaysAndDateFerieAndDeletedAtIsNull(String pays, LocalDate dateFerie);

    /**
     * Compte le nombre de jours fériés d'un pays pour une année
     */
    @Query("SELECT COUNT(j) FROM JourFerie j WHERE j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    long countByPaysAndAnnee(@Param("pays") String pays, @Param("annee") int annee);

    /**
     * Trouve les jours fériés d'un pays tombant un weekend pour une année
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "AND FUNCTION('DAYOFWEEK', j.dateFerie) IN (1, 7)")
    List<JourFerie> findJoursFeriesTombantEnWeekend(@Param("pays") String pays, @Param("annee") int annee);

    /**
     * Vérifie si un slug existe déjà pour un pays et une année
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM JourFerie j " +
            "WHERE j.slug = :slug " +
            "AND j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    boolean existsBySlugAndPaysAndAnnee(@Param("slug") String slug,
                                        @Param("pays") String pays,
                                        @Param("annee") int annee);

    /**
     * Trouve un jour férié par slug, pays et année
     */
    @Query("SELECT j FROM JourFerie j WHERE j.slug = :slug " +
            "AND j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL")
    Optional<JourFerie> findBySlugAndPaysAndAnnee(@Param("slug") String slug,
                                                  @Param("pays") String pays,
                                                  @Param("annee") int annee);


//    @Query("SELECT j FROM JourFerie j WHERE j.slug = :slug " +
//            "AND j.pays = :pays " +
//            "AND YEAR(j.dateFerie) = :annee " +
//            "AND j.deletedAt IS NULL")
//    Optional<JourFerie> findBySlugAndPaysAndAnnee(@Param("slug") String slug,
//                                                  @Param("pays") String pays,
//                                                  @Param("annee") int annee);

    // ========================================
    // 📌 MÉTHODES MULTI-PAYS
    // ========================================

    /**
     * Trouve tous les jours fériés pour plusieurs pays
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays IN :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.pays, j.dateFerie ASC")
    List<JourFerie> findByPaysInAndAnnee(@Param("pays") List<String> pays,
                                         @Param("annee") int annee);

    // ========================================
    // 📊 STATISTIQUES
    // ========================================

    /**
     * Liste tous les pays ayant des jours fériés configurés
     */
    @Query("SELECT DISTINCT j.pays FROM JourFerie j WHERE j.deletedAt IS NULL")
    List<String> findDistinctPays();

    /**
     * Compte les jours fériés par pays pour une année
     */
    @Query("SELECT j.pays, COUNT(j) FROM JourFerie j " +
            "WHERE YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "GROUP BY j.pays")
    List<Object[]> countJoursFeriesParPays(@Param("annee") int annee);

    /**
     * Compte les jours fériés fixes vs mobiles pour un pays
     */
    @Query("SELECT j.estFixe, COUNT(j) FROM JourFerie j " +
            "WHERE j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "GROUP BY j.estFixe")
    List<Object[]> countFixesVsMobiles(@Param("pays") String pays, @Param("annee") int annee);

    // ========================================
    // 🔍 RECHERCHE
    // ========================================

    /**
     * Recherche par mots-clés dans le libellé
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND LOWER(j.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerie> searchByKeyword(@Param("pays") String pays,
                                    @Param("annee") int annee,
                                    @Param("keyword") String keyword);

    /**
     * Trouve les prochains jours fériés à venir pour un pays
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays = :pays " +
            "AND j.dateFerie >= CURRENT_DATE " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerie> findProchainsJoursFeries(@Param("pays") String pays);

    /**
     * Trouve les jours fériés d'un mois spécifique
     */
    @Query("SELECT j FROM JourFerie j WHERE j.pays = :pays " +
            "AND YEAR(j.dateFerie) = :annee " +
            "AND MONTH(j.dateFerie) = :mois " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerie> findByPaysAndMois(@Param("pays") String pays,
                                      @Param("annee") int annee,
                                      @Param("mois") int mois);



    /**
     * Trouve les jours fériés d'une année spécifique
     */
    @Query("SELECT j FROM JourFerie j WHERE YEAR(j.dateFerie) = :annee " +
            "AND j.deletedAt IS NULL " +
            "ORDER BY j.dateFerie ASC")
    List<JourFerie> findByYears(@Param("annee") int annee);

    // ========================================
    // 🗑️ SUPPRESSION
    // ========================================

    /**
     * Supprime (soft delete) tous les jours fériés d'un pays pour une année
     */
    @Query("UPDATE JourFerie j SET j.deletedAt = CURRENT_TIMESTAMP " +
            "WHERE j.pays = :pays AND YEAR(j.dateFerie) = :annee")
    void deleteAllByPaysAndAnnee(@Param("pays") String pays, @Param("annee") int annee);
}