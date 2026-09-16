package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpc.tpcgestpaie.localapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlerteServiceUnifie {

    private final AlerteAbsenceService alerteAbsenceService;
    private final AlerteAnniversaireService alerteAnniversaireService;
    private final AlerteContratService alerteContratService;
    private final AlerteJournalService alerteJournalService;
    private final ObjectMapper objectMapper;

    public AlerteServiceUnifie(AlerteAbsenceService alerteAbsenceService,
                               AlerteAnniversaireService alerteAnniversaireService,
                               AlerteContratService alerteContratService,
                               AlerteJournalService alerteJournalService,
                               ObjectMapper objectMapper) {
        this.alerteAbsenceService = alerteAbsenceService;
        this.alerteAnniversaireService = alerteAnniversaireService;
        this.alerteContratService = alerteContratService;
        this.alerteJournalService = alerteJournalService;
        this.objectMapper = objectMapper;
    }

    /**
     * Récupère toutes les statistiques et données d'alertes dans un JSON unifié
     */
    public ObjectNode getToutesAlertesUnifiees(User currentUser, int jours) {
        ObjectNode resultat = objectMapper.createObjectNode();

        try {
            log.info("🔄 Génération des alertes unifiées pour l'utilisateur: {}", currentUser.getUsername());

            // Alertes Absence
            ObjectNode absenceNode = getAlertesAbsenceUnifiees(currentUser, jours);
            resultat.set("alertesAbsence", absenceNode);

            // Alertes Anniversaire
            ObjectNode anniversaireNode = getAlertesAnniversaireUnifiees(currentUser, jours);
            resultat.set("alertesAnniversaire", anniversaireNode);

            // Alertes Contrat
            ObjectNode contratNode = getAlertesContratUnifiees(currentUser, jours);
            resultat.set("alertesContrat", contratNode);

            // Alertes Journal
            ObjectNode journalNode = getAlertesJournalUnifiees(currentUser, jours);
            resultat.set("alertesJournal", journalNode);

            // Statistiques globales (calculées après avoir toutes les données)
            ObjectNode statsGlobales = calculerStatistiquesGlobales(absenceNode, anniversaireNode, contratNode, journalNode, jours);
            resultat.set("statistiquesGlobales", statsGlobales);

            // Métadonnées
            resultat.set("metadata", getMetadata(currentUser, jours));

            log.info("✅ Génération des alertes unifiées terminée avec succès");

        } catch (Exception e) {
            log.error("❌ Erreur lors de la génération du JSON unifié: {}", e.getMessage());
            // Retourner une structure vide mais valide
            return creerStructureVide(currentUser, jours);
        }

        return resultat;
    }

    private ObjectNode getAlertesAbsenceUnifiees(User currentUser, int jours) {
        ObjectNode absenceNode = objectMapper.createObjectNode();

        try {
            // Statistiques
            ObjectNode statsNode = objectMapper.createObjectNode();
            try {
                AlerteAbsenceService.AbsenceStatistiquesDTO stats =
                        alerteAbsenceService.getStatistiques(currentUser, jours);
                statsNode.put("departsProchains", stats.getDepartsProchains());
                statsNode.put("retoursProchains", stats.getRetoursProchains());
                statsNode.put("absencesEnCours", stats.getAbsencesEnCours());
                statsNode.put("departsAujourdhui", stats.getDepartsAujourdhui());
                statsNode.put("retoursAujourdhui", stats.getRetoursAujourdhui());
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la récupération des stats absence: {}", e.getMessage());
                initialiserStatsVides(statsNode, "departsProchains", "retoursProchains", "absencesEnCours", "departsAujourdhui", "retoursAujourdhui");
            }
            absenceNode.set("statistiques", statsNode);

            // Données détaillées
            ObjectNode donneesNode = objectMapper.createObjectNode();
            donneesNode.set("departsProchains", getArrayNodeSansErreur(() ->
                    alerteAbsenceService.getDepartsProchains(currentUser, jours)));
            donneesNode.set("retoursProchains", getArrayNodeSansErreur(() ->
                    alerteAbsenceService.getRetoursProchains(currentUser, jours)));
            donneesNode.set("absencesEnCours", getArrayNodeSansErreur(() ->
                    alerteAbsenceService.getAbsencesEnCours(currentUser)));
            donneesNode.set("absencesProchaines", getArrayNodeSansErreur(() ->
                    alerteAbsenceService.getAbsencesProchaines(currentUser, jours)));

            absenceNode.set("donnees", donneesNode);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes absence: {}", e.getMessage());
            absenceNode.set("statistiques", creerStatsVides());
            absenceNode.set("donnees", objectMapper.createObjectNode());
        }

        return absenceNode;
    }

    private ObjectNode getAlertesAnniversaireUnifiees(User currentUser, int jours) {
        ObjectNode anniversaireNode = objectMapper.createObjectNode();

        try {
            // Statistiques
            ObjectNode statsNode = objectMapper.createObjectNode();
            try {
                AlerteAnniversaireService.AnniversaireStatistiquesDTO stats =
                        alerteAnniversaireService.getStatistiques(currentUser);
                statsNode.put("totalAujourdhui", stats.getTotalAujourdhui());
                statsNode.put("totalCetteSemaine", stats.getTotalCetteSemaine());
                statsNode.put("totalCeMois", stats.getTotalCeMois());
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la récupération des stats anniversaire: {}", e.getMessage());
                initialiserStatsVides(statsNode, "totalAujourdhui", "totalCetteSemaine", "totalCeMois");
            }
            anniversaireNode.set("statistiques", statsNode);

            // Données détaillées
            ObjectNode donneesNode = objectMapper.createObjectNode();
            donneesNode.set("anniversairesAujourdhui", getArrayNodeSansErreur(() ->
                    alerteAnniversaireService.getAnniversairesAujourdhui(currentUser)));
            donneesNode.set("anniversairesProchains", getArrayNodeSansErreur(() ->
                    alerteAnniversaireService.getAnniversairesProchains(currentUser, jours)));
            donneesNode.set("anniversairesDuMois", getArrayNodeSansErreur(() ->
                    alerteAnniversaireService.getAnniversairesDuMois(currentUser)));

            anniversaireNode.set("donnees", donneesNode);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes anniversaire: {}", e.getMessage());
            anniversaireNode.set("statistiques", creerStatsVides());
            anniversaireNode.set("donnees", objectMapper.createObjectNode());
        }

        return anniversaireNode;
    }

    private ObjectNode getAlertesContratUnifiees(User currentUser, int jours) {
        ObjectNode contratNode = objectMapper.createObjectNode();

        try {
            // Statistiques
            ObjectNode statsNode = objectMapper.createObjectNode();
            try {
                AlerteContratService.ContratStatistiquesDTO stats =
                        alerteContratService.getStatistiques(currentUser, jours);
                statsNode.put("finsEssaiProchaines", stats.getFinsEssaiProchaines());
                statsNode.put("finsContratProchaines", stats.getFinsContratProchaines());
                statsNode.put("anniversairesRecrutement", stats.getAnniversairesRecrutement());
                statsNode.put("finsEssaiAujourdhui", stats.getFinsEssaiAujourdhui());
                statsNode.put("finsContratAujourdhui", stats.getFinsContratAujourdhui());
                statsNode.put("anniversairesAujourdhui", stats.getAnniversairesAujourdhui());
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la récupération des stats contrat: {}", e.getMessage());
                initialiserStatsVides(statsNode, "finsEssaiProchaines", "finsContratProchaines", "anniversairesRecrutement",
                        "finsEssaiAujourdhui", "finsContratAujourdhui", "anniversairesAujourdhui");
            }
            contratNode.set("statistiques", statsNode);

            // Données détaillées
            ObjectNode donneesNode = objectMapper.createObjectNode();
            donneesNode.set("finsEssaiProchaines", getArrayNodeSansErreur(() ->
                    alerteContratService.getFinsEssaiProchaines(currentUser, jours)));
            donneesNode.set("finsContratProchaines", getArrayNodeSansErreur(() ->
                    alerteContratService.getFinsContratProchaines(currentUser, jours)));
            donneesNode.set("anniversairesRecrutementProchains", getArrayNodeSansErreur(() ->
                    alerteContratService.getAnniversairesRecrutementProchains(currentUser, jours)));
            donneesNode.set("toutesAlertesContrat", getArrayNodeSansErreur(() ->
                    alerteContratService.getToutesAlertesContrat(currentUser, jours)));

            contratNode.set("donnees", donneesNode);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes contrat: {}", e.getMessage());
            contratNode.set("statistiques", creerStatsVides());
            contratNode.set("donnees", objectMapper.createObjectNode());
        }

        return contratNode;
    }

    private ObjectNode getAlertesJournalUnifiees(User currentUser, int jours) {
        ObjectNode journalNode = objectMapper.createObjectNode();

        try {
            // Statistiques
            ObjectNode statsNode = objectMapper.createObjectNode();
            try {
                AlerteJournalService.JournalStatistiquesDTO stats =
                        alerteJournalService.getStatistiques(currentUser, jours);
                statsNode.put("totalProchains", stats.getTotalProchains());
                statsNode.put("totalAujourdhui", stats.getTotalAujourdhui());
                statsNode.put("totalCetteSemaine", stats.getTotalCetteSemaine());
                statsNode.put("reunionsProchaines", stats.getReunionsProchaines());
                statsNode.put("entretiensProchains", stats.getEntretiensProchains());
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la récupération des stats journal: {}", e.getMessage());
                initialiserStatsVides(statsNode, "totalProchains", "totalAujourdhui", "totalCetteSemaine",
                        "reunionsProchaines", "entretiensProchains");
            }
            journalNode.set("statistiques", statsNode);

            // Données détaillées
            ObjectNode donneesNode = objectMapper.createObjectNode();
            donneesNode.set("evenementsAujourdhui", getArrayNodeSansErreur(() ->
                    alerteJournalService.getEvenementsAujourdhui(currentUser)));
            donneesNode.set("evenementsProchains", getArrayNodeSansErreur(() ->
                    alerteJournalService.getEvenementsProchains(currentUser, jours)));
            donneesNode.set("evenementsCetteSemaine", getArrayNodeSansErreur(() ->
                    alerteJournalService.getEvenementsCetteSemaine(currentUser)));

            journalNode.set("donnees", donneesNode);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes journal: {}", e.getMessage());
            journalNode.set("statistiques", creerStatsVides());
            journalNode.set("donnees", objectMapper.createObjectNode());
        }

        return journalNode;
    }

    private ObjectNode calculerStatistiquesGlobales(ObjectNode absenceNode, ObjectNode anniversaireNode,
                                                    ObjectNode contratNode, ObjectNode journalNode, int jours) {
        ObjectNode statsGlobales = objectMapper.createObjectNode();

        try {
            // Extraire les valeurs des statistiques de chaque type
            int totalAbsences = absenceNode.get("statistiques").get("departsProchains").asInt() +
                    absenceNode.get("statistiques").get("retoursProchains").asInt();

            int totalAnniversaires = anniversaireNode.get("statistiques").get("totalCetteSemaine").asInt();

            int totalContrats = contratNode.get("statistiques").get("finsEssaiProchaines").asInt() +
                    contratNode.get("statistiques").get("finsContratProchaines").asInt() +
                    contratNode.get("statistiques").get("anniversairesRecrutement").asInt();

            int totalJournaux = journalNode.get("statistiques").get("totalProchains").asInt();

            int totalAlertes = totalAbsences + totalAnniversaires + totalContrats + totalJournaux;

            statsGlobales.put("totalAlertes", totalAlertes);
            statsGlobales.put("alertesAujourdhui",
                    absenceNode.get("statistiques").get("departsAujourdhui").asInt() +
                            absenceNode.get("statistiques").get("retoursAujourdhui").asInt() +
                            anniversaireNode.get("statistiques").get("totalAujourdhui").asInt() +
                            contratNode.get("statistiques").get("finsEssaiAujourdhui").asInt() +
                            contratNode.get("statistiques").get("finsContratAujourdhui").asInt() +
                            journalNode.get("statistiques").get("totalAujourdhui").asInt()
            );
            statsGlobales.put("alertesCetteSemaine", totalAlertes);
            statsGlobales.put("periodeJours", jours);

        } catch (Exception e) {
            log.warn("Erreur lors du calcul des statistiques globales: {}", e.getMessage());
            statsGlobales.put("totalAlertes", 0);
            statsGlobales.put("alertesAujourdhui", 0);
            statsGlobales.put("alertesCetteSemaine", 0);
            statsGlobales.put("periodeJours", jours);
        }

        return statsGlobales;
    }

    // Méthodes utilitaires pour gérer les erreurs
    private ArrayNode getArrayNodeSansErreur(DataSupplier supplier) {
        try {
            return objectMapper.valueToTree(supplier.get());
        } catch (Exception e) {
            log.warn("Erreur lors de la récupération des données: {}", e.getMessage());
            return objectMapper.createArrayNode();
        }
    }

    private ObjectNode creerStatsVides() {
        ObjectNode stats = objectMapper.createObjectNode();
        stats.put("vide", true);
        return stats;
    }

    private void initialiserStatsVides(ObjectNode statsNode, String... champs) {
        for (String champ : champs) {
            statsNode.put(champ, 0);
        }
    }

    private ObjectNode creerStructureVide(User currentUser, int jours) {
        ObjectNode resultat = objectMapper.createObjectNode();
        resultat.set("statistiquesGlobales", objectMapper.createObjectNode()
                .put("totalAlertes", 0)
                .put("alertesAujourdhui", 0)
                .put("alertesCetteSemaine", 0)
                .put("periodeJours", jours));
        resultat.set("alertesAbsence", objectMapper.createObjectNode());
        resultat.set("alertesAnniversaire", objectMapper.createObjectNode());
        resultat.set("alertesContrat", objectMapper.createObjectNode());
        resultat.set("alertesJournal", objectMapper.createObjectNode());
        resultat.set("metadata", getMetadata(currentUser, jours));
        return resultat;
    }

    private ObjectNode getMetadata(User currentUser, int jours) {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("dateGeneration", LocalDate.now().toString());
        metadata.put("utilisateur", currentUser.getUsername());
        metadata.put("userId", currentUser.getId());
        metadata.put("periodeJours", jours);
        metadata.put("timestamp", System.currentTimeMillis());
        return metadata;
    }

    /**
     * Version simplifiée pour les tableaux de bord
     */
    public Map<String, Object> getAlertesResume(User currentUser, int jours) {
        Map<String, Object> resume = new HashMap<>();

        try {
            ObjectNode donneesCompletes = getToutesAlertesUnifiees(currentUser, jours);
            resume.put("donnees", donneesCompletes);

            // Extraire les KPI principaux pour affichage rapide
            ObjectNode kpis = objectMapper.createObjectNode();
            kpis.set("statistiquesGlobales", donneesCompletes.get("statistiquesGlobales"));

            resume.put("kpis", kpis);
            resume.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            log.error("Erreur lors de la génération du résumé: {}", e.getMessage());
            resume.put("donnees", objectMapper.createObjectNode());
            resume.put("kpis", objectMapper.createObjectNode().set("statistiquesGlobales", objectMapper.createObjectNode()));
            resume.put("timestamp", System.currentTimeMillis());
        }

        return resume;
    }

    @FunctionalInterface
    private interface DataSupplier {
        Object get();
    }
}