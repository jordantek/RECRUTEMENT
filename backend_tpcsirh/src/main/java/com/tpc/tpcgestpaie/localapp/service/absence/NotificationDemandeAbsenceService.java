package com.tpc.tpcgestpaie.localapp.service.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.NotificationDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.absence.NotificationDemandeAbsenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationDemandeAbsenceService {

    private final NotificationDemandeAbsenceRepository notificationRepository;
    private final EmailNotificationService emailService; // ✅ AJOUTÉ

    /**
     * Récupérer les notifications non lues d'un employé
     */
    public List<NotificationDemandeAbsence> getNotificationsNonLues(Long employeId) {
        return notificationRepository.findNotificationsNonLues(employeId);
    }

    /**
     * Compter les notifications non lues
     */
    public long compterNotificationsNonLues(Long employeId) {
        return notificationRepository.countByDestinataireIdAndLu(employeId, false);
    }

    /**
     * Marquer une notification comme lue
     */
    public void marquerCommeLue(Long notificationId) {
        NotificationDemandeAbsence notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        notification.setLu(true);
        notification.setDateLecture(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Récupérer l'historique complet des notifications
     */
    public List<NotificationDemandeAbsence> getHistoriqueNotifications(Long employeId) {
        return notificationRepository.findByDestinataireIdOrderByDateEnvoiDesc(employeId);
    }

    // ========================================
    // ✅ MÉTHODES D'ENVOI (NOUVELLES)
    // ========================================

    /**
     * Envoyer une notification avec enregistrement en base + email
     */
    public void envoyerNotificationDemande(
            String emailDestinataire,
            String sujet,
            String message) {

        try {
            // Envoyer l'email
            emailService.envoyerNotificationDemande(emailDestinataire, sujet, message);
        } catch (Exception e) {
            // Logger l'erreur mais ne pas bloquer le processus
            System.err.println("Erreur envoi email à " + emailDestinataire + ": " + e.getMessage());
        }
    }

    /**
     * Envoyer une notification avec enregistrement en base de données
     */
    public void envoyerNotificationAvecEnregistrement(
            DemandeAbsence demande,
            Employe destinataire,
            String typeNotification,
            String message) {

        // 1. Enregistrer la notification en base
        NotificationDemandeAbsence notification = new NotificationDemandeAbsence();
        notification.setDemandeAbsence(demande);
        notification.setDestinataire(destinataire);
        notification.setTypeNotification(typeNotification);
        notification.setMessage(message);
        notification.setLu(false);
        notificationRepository.save(notification);

        // 2. Envoyer l'email
        try {
            emailService.envoyerNotificationDemande(
                    destinataire.getEmail(),
                    "Demande d'absence - " + typeNotification,
                    message
            );
        } catch (Exception e) {
            System.err.println("Erreur envoi email à " + destinataire.getEmail() + ": " + e.getMessage());
        }
    }

    /**
     * Notifier plusieurs destinataires à la fois
     */
    public void envoyerNotificationMultiple(
            DemandeAbsence demande,
            List<Employe> destinataires,
            String typeNotification,
            String message) {

        for (Employe destinataire : destinataires) {
            envoyerNotificationAvecEnregistrement(demande, destinataire, typeNotification, message);
        }
    }

    /**
     * Créer une notification sans envoyer d'email (juste enregistrement)
     */
    public void creerNotificationSilencieuse(
            DemandeAbsence demande,
            Employe destinataire,
            String typeNotification,
            String message) {

        NotificationDemandeAbsence notification = new NotificationDemandeAbsence();
        notification.setDemandeAbsence(demande);
        notification.setDestinataire(destinataire);
        notification.setTypeNotification(typeNotification);
        notification.setMessage(message);
        notification.setLu(false);
        notificationRepository.save(notification);
    }

    /**
     * Supprimer toutes les notifications d'une demande
     */
    public void supprimerNotificationsDemande(Long demandeId) {
        List<NotificationDemandeAbsence> notifications =
                notificationRepository.findByDemandeAbsenceIdOrderByDateEnvoiDesc(demandeId);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * Marquer toutes les notifications d'un employé comme lues
     */
    public void marquerToutesCommeLues(Long employeId) {
        List<NotificationDemandeAbsence> notifications =
                notificationRepository.findNotificationsNonLues(employeId);

        LocalDateTime now = LocalDateTime.now();
        for (NotificationDemandeAbsence notification : notifications) {
            notification.setLu(true);
            notification.setDateLecture(now);
        }

        notificationRepository.saveAll(notifications);
    }
}