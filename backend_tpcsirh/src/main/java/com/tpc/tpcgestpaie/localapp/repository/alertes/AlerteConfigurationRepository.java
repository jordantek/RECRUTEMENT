package com.tpc.tpcgestpaie.localapp.repository.alertes;

import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlerteConfigurationRepository extends JpaRepository<AlerteConfiguration, Long> {

    // Configurations d'un utilisateur spécifique
    @Query("SELECT ac FROM AlerteConfiguration ac WHERE ac.user.id = :userId ORDER BY ac.typeAlerte")
    List<AlerteConfiguration> findByUserId(@Param("userId") Long userId);

    // Configuration spécifique pour un type d'alerte et un utilisateur
    @Query("SELECT ac FROM AlerteConfiguration ac WHERE ac.user.id = :userId AND ac.typeAlerte = :typeAlerte")
    Optional<AlerteConfiguration> findByUserIdAndType(@Param("userId") Long userId,
                                                      @Param("typeAlerte") AlerteConfiguration.TypeAlerte typeAlerte);

    // Vérifier l'existence d'une configuration
    boolean existsByTypeAlerteAndUserId(AlerteConfiguration.TypeAlerte typeAlerte, Long userId);

    // Configurations par défaut (pour initialisation)
    @Query("SELECT ac FROM AlerteConfiguration ac WHERE ac.user IS NULL")
    List<AlerteConfiguration> findDefaultConfigurations();
}