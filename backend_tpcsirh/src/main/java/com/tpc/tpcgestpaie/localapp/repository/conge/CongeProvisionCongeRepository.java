package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CongeProvisionCongeRepository extends JpaRepository<ProvisionConge, Long> {

    /**
     * Vérifie si une provision existe déjà pour ce mois (évite doublons batch)
     */
    boolean existsByEmployeIdAndMoisReference(Long employeId, String moisReference);

    /**
     * Récupère toutes les provisions d'un employé, ordonnées chronologiquement
     * Pour l'historique complet
     */
    List<ProvisionConge> findByEmployeIdOrderByMoisReferenceAsc(Long employeId);

    /**
     * Récupère les provisions avec solde > 0, ordonnées FIFO (plus anciennes d'abord)
     * Pour la consommation
     */
    @Query("SELECT p FROM ProvisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND p.joursRestant > 0 " +
            "AND p.statut != 'ANNULE' " +
            "ORDER BY p.moisReference ASC")
    List<ProvisionConge> findDisponiblesPourConsommation(@Param("employeId") Long employeId);

    /**
     * Récupère le premier mois disponible avec solde > 0 (FIFO)
     */
    @Query("SELECT p FROM ProvisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND p.joursRestant > 0 " +
            "AND p.statut != 'ANNULE' " +
            "ORDER BY p.moisReference ASC")
    Optional<ProvisionConge> findFirstDisponible(@Param("employeId") Long employeId);

    /**
     * Somme des jours restants (solde actuel)
     */
    @Query("SELECT COALESCE(SUM(p.joursRestant), 0) FROM ProvisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND p.statut != 'ANNULE'")
    BigDecimal getTotalJoursRestantsByEmploye(@Param("employeId") Long employeId);

    /**
     * Provisions jusqu'à un mois donné (pour solde à date)
     */
    @Query("SELECT p FROM ProvisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND p.moisReference <= :moisReference " +
            "ORDER BY p.moisReference ASC")
    List<ProvisionConge> findByEmployeIdAndMoisReferenceLessThanEqual(
            @Param("employeId") Long employeId,
            @Param("moisReference") String moisReference
    );

    Optional<ProvisionConge> findByEmployeIdAndMoisReference(Long employeId, String moisReference);


    /**
     * 🎯 REQUÊTE CRITIQUE FIFO : Priorité provision initiale, puis mois croissants
     */
    @Query("""
        SELECT p FROM ProvisionConge p 
        WHERE p.employe.id = :employeId 
        AND p.joursRestant > 0 
        AND p.statut IN ('ACTIF', 'PARTIELLEMENT_CONSOMME')
        AND p.typeProvision != 'REGULARISATION'
        ORDER BY 
            CASE 
                WHEN p.typeProvision IN ('SYNTHESE_INITIALE', 'SYNTHESE_AVEC_HISTORIQUE') THEN 0 
                ELSE 1 
            END ASC,
            p.moisReference ASC
        """)
    List<ProvisionConge> findDisponiblesPourConsommationFifo(@Param("employeId") Long employeId);



    long countByEmployeId(Long employeId);

    @Query("SELECT p FROM ProvisionConge p WHERE p.employe.id = :employeId AND p.typeProvision IN ('SYNTHESE_INITIALE', 'SYNTHESE_AVEC_HISTORIQUE')")
    Optional<ProvisionConge> findProvisionInitialeByEmploye(@Param("employeId") Long employeId);

    @Query("""
        SELECT COUNT(p) > 0 FROM ProvisionConge p 
        WHERE p.employe.id = :employeId 
        AND p.joursAcquis IS NOT NULL 
        AND p.joursAcquis > 0
        """)
    boolean existsValidProvisionsByEmployeId(@Param("employeId") Long employeId);
}