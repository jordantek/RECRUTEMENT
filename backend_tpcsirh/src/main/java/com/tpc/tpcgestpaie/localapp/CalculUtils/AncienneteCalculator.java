package com.tpc.tpcgestpaie.localapp.CalculUtils;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.anciennete.AncienneteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AncienneteCalculator {

    @Autowired
    private ContratEmployeRepository contratEmployeRepository;
    @Autowired
    private AncienneteSettingService ancienneteSettingService;

    /**
     * Calcule l'ancienneté totale d'un employé dans une entreprise
     * en tenant compte des interruptions entre contrats et de la date d'embauche
     */
    public double calculerAnciennete(List<ContratEmploye> contrats, int nombreMoisEcart) {
        if (contrats == null || contrats.isEmpty()) {
            return 0.0;
        }

        // Trier les contrats par date de début
        contrats.sort((c1, c2) -> {
            LocalDate d1 = c1.getDate_debut();
            LocalDate d2 = c2.getDate_debut();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return -1;
            if (d2 == null) return 1;
            return d1.compareTo(d2);
        });

        long totalJours = 0;
        LocalDate aujourdHui = LocalDate.now();
        LocalDate finPrecedent = null;

        // Récupérer le premier contrat pour vérifier dateEmbauche
        ContratEmploye premierContrat = contrats.get(0);
        LocalDate dateDepart = null;

        // Déterminer le point de départ du calcul
        if (premierContrat.getDateEmbauche() != null) {
            dateDepart = premierContrat.getDateEmbauche();
            System.out.println("Date d'embauche trouvée: " + dateDepart);
        } else if (premierContrat.getDate_debut() != null) {
            dateDepart = premierContrat.getDate_debut();
            System.out.println("Pas de date d'embauche, utilisation de date_debut: " + dateDepart);
        }

        if (dateDepart == null) {
            System.out.println("Impossible de déterminer le point de départ du calcul");
            return 0.0;
        }

        for (int i = 0; i < contrats.size(); i++) {
            ContratEmploye contrat = contrats.get(i);
            LocalDate debut = contrat.getDate_debut();
            LocalDate fin = contrat.getDate_fin();

            if (debut == null) continue;

            // Pour le dernier contrat ou si dateFin nulle, on prend aujourd'hui
            boolean isDernierContrat = (i == contrats.size() - 1);
            if (fin == null || isDernierContrat) {
                fin = aujourdHui;
                System.out.println("Dernier contrat ou date_fin nulle → fin = aujourd'hui (" + aujourdHui + ")");
            }

            // Vérifier l'écart avec le contrat précédent (sauf pour le premier)
            if (i > 0 && finPrecedent != null) {
                long ecartMois = ChronoUnit.MONTHS.between(finPrecedent, debut);

                System.out.println("Écart entre contrat " + i + " et " + (i+1) +
                        ": " + ecartMois + " mois (limite: " + nombreMoisEcart + " mois)");

                if (ecartMois >= nombreMoisEcart) {
                    // Écart trop grand : RESET TOTAL - on perd toute l'ancienneté
                    totalJours = 0;
                    dateDepart = debut; // Le nouveau point de départ est ce contrat
                    System.out.println("✗ Écart trop grand - RESET TOTAL de l'ancienneté");
                    System.out.println("  Nouveau point de départ: " + dateDepart);
                }
            }

            // Calculer la durée pour ce contrat
            long joursContrat;

            if (i == 0) {
                // Pour le premier contrat, on calcule depuis dateDepart jusqu'à fin
                joursContrat = ChronoUnit.DAYS.between(dateDepart, fin) + 1;
                System.out.println("Contrat 1 (depuis date d'embauche): " + dateDepart + " → " + fin +
                        " = " + joursContrat + " jours");
            } else {
                // Pour les contrats suivants (si pas de reset), on calcule normalement
                joursContrat = ChronoUnit.DAYS.between(debut, fin) + 1;
                System.out.println("Contrat " + (i+1) + ": " + debut + " → " + fin +
                        " = " + joursContrat + " jours");
            }

            totalJours += joursContrat;
            System.out.println("  Total cumulé: " + totalJours + " jours");

            // Mettre à jour la fin du contrat précédent
            finPrecedent = fin;
        }

        // Convertir jours en années décimales
        double annees = totalJours / 365.25;
        System.out.println("\n=== RÉSULTAT FINAL ===");
        System.out.println("Ancienneté totale: " + totalJours + " jours = " + String.format("%.2f", annees) + " années");

        return annees;
    }

    /**
     * Version avec injection du Repository et récupération automatique de l'écart
     */
    public double calculerAnciennete(Long employeId, Long entrepriseId) {
        // Vérifier si le paramètre d'ancienneté existe pour l'entreprise
        if (!ancienneteSettingService.getByCompanyId(entrepriseId).isPresent()) {
            System.err.println("❌ Paramètre d'ancienneté non trouvé pour l'entreprise ID: " + entrepriseId);
            return 0.0;
        }

        // Récupérer l'écart en mois configuré pour l'entreprise
        Integer ecart = ancienneteSettingService.getEcartMoisForCompany(entrepriseId);

        if (ecart == null) {
            System.err.println("❌ Écart mois non configuré pour l'entreprise ID: " + entrepriseId);
            return 0.0;
        }

        // Récupérer tous les contrats de l'employé dans cette entreprise
        List<ContratEmploye> contrats = contratEmployeRepository
                .findByEmployeIdAndCompanyId(employeId, entrepriseId);
        return calculerAnciennete(contrats, ecart);
    }

    /**
     * Version avec injection du Repository et écart en paramètre
     */
    public double calculerAnciennete(Long employeId, Long entrepriseId, int nombreMoisEcart) {
        List<ContratEmploye> contrats = contratEmployeRepository
                .findByEmployeIdAndCompanyId(employeId, entrepriseId);

        return calculerAnciennete(contrats, nombreMoisEcart);
    }

    /**
     * Version avec repository en paramètre
     */
    public double calculerAnciennete(Long employeId, Long entrepriseId, int nombreMoisEcart,
                                     ContratEmployeRepository repository) {
        List<ContratEmploye> contrats = repository
                .findByEmployeIdAndCompanyId(employeId, entrepriseId);

        return calculerAnciennete(contrats, nombreMoisEcart);
    }

    /**
     * Méthode utilitaire pour comprendre le calcul en détail
     */
    public void afficherDetailCalcul(List<ContratEmploye> contrats, int nombreMoisEcart) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         DÉTAIL DU CALCUL D'ANCIENNETÉ                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Nombre de contrats: " + contrats.size());
        System.out.println("Écart maximum autorisé: " + nombreMoisEcart + " mois");
        System.out.println("Date du jour: " + LocalDate.now());

        contrats.sort((c1, c2) -> {
            LocalDate d1 = c1.getDate_debut();
            LocalDate d2 = c2.getDate_debut();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return -1;
            if (d2 == null) return 1;
            return d1.compareTo(d2);
        });

        // Afficher la date d'embauche si elle existe
        ContratEmploye premierContrat = contrats.get(0);
        if (premierContrat.getDateEmbauche() != null) {
            System.out.println("\n📅 Date d'embauche initiale: " + premierContrat.getDateEmbauche());
        }

        System.out.println("\n────────────────────────────────────────────────────────────");

        for (int i = 0; i < contrats.size(); i++) {
            ContratEmploye contrat = contrats.get(i);
            System.out.println("\n📋 Contrat " + (i+1) + ":");
            System.out.println("   Début: " + contrat.getDate_debut());
            System.out.println("   Fin: " + (contrat.getDate_fin() != null ? contrat.getDate_fin() : "En cours"));

            if (i == 0 && premierContrat.getDateEmbauche() != null) {
                System.out.println("   ℹ️  Calcul depuis date d'embauche: " + premierContrat.getDateEmbauche());
            }

            if (i > 0) {
                ContratEmploye precedent = contrats.get(i-1);
                LocalDate finPrecedent = precedent.getDate_fin();
                if (finPrecedent != null && contrat.getDate_debut() != null) {
                    long ecartMois = ChronoUnit.MONTHS.between(finPrecedent, contrat.getDate_debut());
                    System.out.println("   ⏱️  Écart avec contrat précédent: " + ecartMois + " mois");

                    if (ecartMois >= nombreMoisEcart) {
                        System.out.println("   ✗ ÉCART TROP GRAND → Ancienneté réinitialisée");
                    } else {
                        System.out.println("   ✓ Écart acceptable → Ancienneté cumulée");
                    }
                }
            }
        }

        System.out.println("\n────────────────────────────────────────────────────────────");
        double resultat = calculerAnciennete(contrats, nombreMoisEcart);
        System.out.println("\n🎯 ANCIENNETÉ FINALE: " + String.format("%.2f", resultat) + " années");
        System.out.println("════════════════════════════════════════════════════════════\n");
    }
}