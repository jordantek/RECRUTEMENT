package com.tpc.tpcgestpaie.localapp.repository.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.NotificationDemandeAbsence;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationDemandeAbsenceRepository extends JpaRepository<NotificationDemandeAbsence, Long> {

    /**
     * Notifications non lues d'un employé
     */
    List<NotificationDemandeAbsence> findByDestinataireIdAndLuOrderByDateEnvoiDesc(Long destinataireId, boolean lu);

    /**
     * Toutes les notifications d'un employé
     */
    List<NotificationDemandeAbsence> findByDestinataireIdOrderByDateEnvoiDesc(Long destinataireId);

    /**
     * Compter les notifications non lues
     */
    long countByDestinataireIdAndLu(Long destinataireId, boolean lu);

    /**
     * Notifications non lues (alias pour plus de clarté)
     */
    @Query("SELECT n FROM NotificationDemandeAbsence n WHERE n.destinataire.id = :destinataireId AND n.lu = false ORDER BY n.dateEnvoi DESC")
    List<NotificationDemandeAbsence> findNotificationsNonLues(@Param("destinataireId") Long destinataireId);

    /**
     * Notifications d'une demande spécifique
     */
    @Query("SELECT n FROM NotificationDemandeAbsence n WHERE n.demandeAbsence.id = :demandeId ORDER BY n.dateEnvoi DESC")
    List<NotificationDemandeAbsence> findByDemandeAbsenceIdOrderByDateEnvoiDesc(@Param("demandeId") Long demandeId);

    /**
     * Notifications par type
     */
    List<NotificationDemandeAbsence> findByDestinataireIdAndTypeNotificationOrderByDateEnvoiDesc(
            Long destinataireId,
            String typeNotification
    );

    /**
     * Notifications récentes (dernières 24h)
     */
    @Query("SELECT n FROM NotificationDemandeAbsence n " +
            "WHERE n.destinataire.id = :destinataireId " +
            "AND n.dateEnvoi > :dateLimite " +
            "ORDER BY n.dateEnvoi DESC")
    List<NotificationDemandeAbsence> findNotificationsRecentes(
            @Param("destinataireId") Long destinataireId,
            @Param("dateLimite") LocalDateTime dateLimite);

    /**
     * Supprimer les notifications anciennes (plus de X jours)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationDemandeAbsence n " +
            "WHERE n.dateEnvoi < :dateLimite " +
            "AND n.lu = true")
    void deleteOldNotifications(@Param("dateLimite") LocalDateTime dateLimite);

}