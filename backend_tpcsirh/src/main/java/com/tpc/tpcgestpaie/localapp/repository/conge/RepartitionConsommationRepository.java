package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.RepartitionConsommation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepartitionConsommationRepository extends JpaRepository<RepartitionConsommation, Long> {

    /**
     * Récupère les répartitions actives d'une demande (non annulées)
     * Ordonnées par mois de provision (chronologique)
     */
    @Query("SELECT r FROM RepartitionConsommation r " +
            "JOIN FETCH r.provisionConge p " +
            "WHERE r.demandeAbsence.id = :demandeId " +
            "AND r.estAnnule = false " +
            "ORDER BY p.moisReference ASC")
    List<RepartitionConsommation> findActivesByDemandeOrderByMois(
            @Param("demandeId") Long demandeId
    );




    /**
     * Récupère les répartitions actives d'un employé jusqu'à une date
     * Pour calcul du solde à date
     */
    @Query("SELECT r FROM RepartitionConsommation r " +
            "JOIN r.provisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND r.estAnnule = false " +
            "AND r.dateConsommation <= :date " +
            "ORDER BY r.dateConsommation ASC")
    List<RepartitionConsommation> findActivesByEmployeJusquADate(
            @Param("employeId") Long employeId,
            @Param("date") LocalDateTime date
    );

    /**
     * Récupère les répartitions d'une provision spécifique
     * Pour voir qui a consommé sur ce mois
     */
    List<RepartitionConsommation> findByProvisionCongeIdAndEstAnnuleFalse(Long provisionId);

    /**
     * Vérifie si une demande a des répartitions actives
     */
    boolean existsByDemandeAbsenceIdAndEstAnnuleFalse(Long demandeId);

    @Query("SELECT r FROM RepartitionConsommation r " +
            "JOIN r.provisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND r.estAnnule = true")
    List<RepartitionConsommation> findAnnulationsByEmploye(@Param("employeId") Long employeId);

    @Query("SELECT r FROM RepartitionConsommation r " +
            "JOIN r.provisionConge p " +
            "WHERE p.employe.id = :employeId " +
            "AND r.dateConsommation BETWEEN :debut AND :fin " +
            "AND r.estAnnule = false")
    List<RepartitionConsommation> findByEmployeAndPeriode(
            @Param("employeId") Long employeId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);


    /**
     * Récupère toutes les répartitions d'une demande (même annulées)
     * Pour historique complet avec annulations
     */
    @Query("SELECT r FROM RepartitionConsommation r " +
            "LEFT JOIN FETCH r.provisionConge p " +  // 🆕 LEFT JOIN FETCH crucial
            "WHERE r.demandeAbsence.id = :demandeId " +
            "ORDER BY p.moisReference ASC")
    List<RepartitionConsommation> findAllByDemandeOrderByMois(@Param("demandeId") Long demandeId);
}