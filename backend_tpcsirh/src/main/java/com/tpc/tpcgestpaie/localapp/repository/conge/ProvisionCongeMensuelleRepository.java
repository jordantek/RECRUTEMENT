package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionCongeMensuelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProvisionCongeMensuelleRepository extends JpaRepository<ProvisionCongeMensuelle, Long> {

    Optional<ProvisionCongeMensuelle> findByEmployeIdAndAnneeMois(Long employeId, String anneeMois);

    boolean existsByEmployeIdAndAnneeMois(Long employeId, String anneeMois);

    Optional<ProvisionCongeMensuelle> findTopByEmployeIdOrderByAnneeMoisDesc(Long employeId);

    List<ProvisionCongeMensuelle> findByEmployeIdOrderByAnneeMoisAsc(Long employeId);

    List<ProvisionCongeMensuelle> findByEmployeIdOrderByAnneeMoisDesc(Long employeId);

    // 🆕 Méthode corrigée pour récupérer les provisions disponibles (FIFO)
    @Query("SELECT p FROM ProvisionCongeMensuelle p WHERE p.employe.id = :employeId " +
            "AND (p.estConsommee = false OR p.joursRestantsApresConso > 0) " +
            "ORDER BY p.anneeMois ASC")
    List<ProvisionCongeMensuelle> findProvisionsDisponiblesByEmployeIdOrderByAnneeMoisAsc(@Param("employeId") Long employeId);

    // Alternative avec query native si nécessaire
    @Query(value = "SELECT * FROM provision_conge_mensuelle " +
            "WHERE employe_id = :employeId " +
            "AND (est_consommee = false OR jours_restants_apres_conso > 0) " +
            "ORDER BY annee_mois ASC", nativeQuery = true)
    List<ProvisionCongeMensuelle> findProvisionsDisponiblesNative(@Param("employeId") Long employeId);
}