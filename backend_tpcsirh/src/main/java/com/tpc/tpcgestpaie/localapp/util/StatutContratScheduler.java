package com.tpc.tpcgestpaie.localapp.util;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatutContratRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StatutContratScheduler {

    private final StatutContratRepository statutContratRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    public StatutContratScheduler(StatutContratRepository statutContratRepository,
                                  ContratEmployeRepository contratEmployeRepository) {
        this.statutContratRepository = statutContratRepository;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    // 🔹 Exécuté tous les jours à minuit
//    @Scheduled(cron = "0 0 0 * * ?")
// chaque 30 secondes
    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional
    public void appliquerStatuts() {
        LocalDate aujourdHui = LocalDate.now();

        // 🔹 Récupérer tous les statuts dont la dateEffet <= aujourd'hui et non actifs
        List<StatutContrat> statutsAActiver = statutContratRepository
                .findStatutsAActiver(aujourdHui);

        for (StatutContrat statut : statutsAActiver) {
            ContratEmploye contrat = statut.getContratEmploye();

            if (statut.getDateFinContrat() != null) statut.setActif(false);

            if (contrat == null) continue; // Sécurité

            // 🔹 Désactiver l'ancien statut actif (sauf initialisation)
            List<StatutContrat> anciens = statutContratRepository.findByContratEmployeId(contrat.getId());
            for (StatutContrat ancien : anciens) {
                if (ancien.isActif() && !ancien.isInitialisation()) {
                    ancien.setActif(false);
                    statutContratRepository.save(ancien);
                }
            }
            // 🔹 Mise à jour des champs du contrat depuis le statut
            if (statut.getCategorieEmploye() != null) contrat.setCategorieEmploye(statut.getCategorieEmploye());
            if (statut.getTypeContrat() != null) contrat.setType_contrat(statut.getTypeContrat());
            if (statut.getDepartement() != null) contrat.setDepartement(statut.getDepartement());
            if (statut.getPoste() != null) contrat.setPoste(statut.getPoste());
            if (statut.getDateFinContrat() != null) contrat.setDate_fin(statut.getDateFinContrat());
            if (statut.getSalaireBase() != null) contrat.setSalaire_base(statut.getSalaireBase());
            if (statut.getSalaireBrut() != null) contrat.setSalaire_brut(statut.getSalaireBrut());

            // 🔹 Activer le statut uniquement si dateEffet <= aujourd'hui
            if (!statut.getDateEffet().isAfter(aujourdHui)) {
                statut.setActif(true);
            }

            //if (statut.getDateFinContrat() != null) statut.setActif(false);

                        // 🔹 Sauvegarde des changements
            contratEmployeRepository.save(contrat);
            statutContratRepository.save(statut);
        }
    }
}
