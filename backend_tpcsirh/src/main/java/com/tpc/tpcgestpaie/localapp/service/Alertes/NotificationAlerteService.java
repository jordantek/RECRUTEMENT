package com.tpc.tpcgestpaie.localapp.service.Alertes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class NotificationAlerteService {

    @Value("${app.notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notification.slack.enabled:false}")
    private boolean slackEnabled;

    public void envoyerNotification(String message, TypeAlerte typeAlerte) {
        // Notification par email
        if (emailEnabled) {
            envoyerEmail(message, typeAlerte);
        }

        // Notification Slack/Teams
        if (slackEnabled) {
            envoyerSlack(message, typeAlerte);
        }

        // Log dans la base de données
        sauvegarderNotification(message, typeAlerte);
    }

    private void envoyerEmail(String message, TypeAlerte typeAlerte) {
        // Implémentation pour l'envoi d'email
//        log.info("Email envoyé: {}", message);
    }

    private void envoyerSlack(String message, TypeAlerte typeAlerte) {
        // Implémentation pour Slack/Teams
//        log.info("Notification Slack: {}", message);
    }

    private void sauvegarderNotification(String message, TypeAlerte typeAlerte) {
        // Sauvegarde en base pour historique
    }
}

enum TypeAlerte {
    ANNIVERSAIRE,
    ANNIVERSAIRE_JOUR_J
}