package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.solde.SoldesEntrepriseDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.solde.SoldesEntrepriseDTO.SoldeEmployeResumeDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoldesEntrepriseService {

    private final EmployeRepository employeRepository;
    private final SoldeCongeService soldeService;
    private final CongeProvisionCongeRepository provisionRepository;

    @Transactional(readOnly = true) // ⭐ Transaction globale pour éviter lazy loading
    public SoldesEntrepriseDTO getSoldesParEntreprise(Long companyId) {
        log.info("📊 Consultation soldes entreprise: {}", companyId);

        // 1. Récupérer employés avec Company chargée (EAGER ou fetch join)
        List<Employe> employes = employeRepository.findByCompanyIdWithCompany(companyId);

        if (employes.isEmpty()) {
            throw new RuntimeException("Aucun employé trouvé pour l'entreprise " + companyId);
        }

        // ⭐ Forcer le chargement de Company pour le premier (évite lazy loading)
        Employe firstEmp = employes.get(0);
        String companyName = firstEmp.getCompany() != null ? firstEmp.getCompany().getName() : "Inconnu";

        // 2. Traiter chaque employé (dans la même transaction)
        List<SoldeEmployeResumeDTO> soldesEmployes = employes.stream()
                .map(this::construireSoldeEmployeSafe)
                .collect(Collectors.toList());

        // 3. Calculs synthèse
        int totalEmployes = employes.size();
        long initialises = soldesEmployes.stream()
                .filter(SoldeEmployeResumeDTO::getEstInitialise)
                .count();

        BigDecimal totalJours = BigDecimal.ZERO;
        BigDecimal totalValeur = BigDecimal.ZERO;

        for (SoldeEmployeResumeDTO s : soldesEmployes) {
            if (s.getSoldeJoursDisponibles() != null) {
                totalJours = totalJours.add(s.getSoldeJoursDisponibles());
            }
            if (s.getValeurEstimeeSolde() != null) {
                totalValeur = totalValeur.add(s.getValeurEstimeeSolde());
            }
        }

        BigDecimal moyenneJours = initialises > 0 ?
                totalJours.divide(BigDecimal.valueOf(initialises), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        return SoldesEntrepriseDTO.builder()
                .companyId(companyId)
                .nomEntreprise(companyName) // ⭐ Utilise la valeur déjà récupérée
                .totalEmployes(totalEmployes)
//                .employesInitialises((int) initialises)
//                .employesNonInitialises(totalEmployes - (int) initialises)
                .totalJoursDisponibles(totalJours)
                .totalValeurEstimee(totalValeur)
                .moyenneJoursParEmploye(moyenneJours)
                .employes(soldesEmployes)
                .build();
    }

    /**
     * ⭐ SAFE : Vérifie avant d'appeler soldeService
     */
    private SoldeEmployeResumeDTO construireSoldeEmployeSafe(Employe employe) {

        // 1. Vérification : a-t-il des provisions ?
        long nbProvisions = provisionRepository.countByEmployeId(employe.getId());

        if (nbProvisions == 0) {
            log.debug("Employe {}: Non initialisé", employe.getId());
            return buildNonInitialise(employe);
        }

        // 2. Vérification : les provisions ont-elles des données valides ?
        boolean hasValidData = provisionRepository.existsValidProvisionsByEmployeId(employe.getId());
        if (!hasValidData) {
            log.warn("Employe {}: Provisions existent mais données invalides", employe.getId());
            return buildErreur(employe, "Données invalides (vérifier jours acquis)");
        }

        // 3. Appel service avec protection
        try {
            var solde = soldeService.getSoldeAdate(employe.getId(), LocalDate.now());

            return SoldeEmployeResumeDTO.builder()
                    .employeId(employe.getId())
                    .matricule(employe.getMatricule())
                    .nomEmploye(solde.getNomEmploye())
                    .totalJoursAcquis(solde.getTotalJoursAcquis())
                    .totalJoursConsommes(solde.getTotalJoursConsommes())
                    .soldeJoursDisponibles(solde.getSoldeJoursDisponibles())
                    .valeurEstimeeSolde(solde.getValeurEstimeeSolde())
                    .montantTotalProvisionne(solde.getMontantTotalProvisionne())
                    .montantTotalConsomme(solde.getMontantTotalConsomme())
                    .estInitialise(true)
                    .alerteSoldeFaible(solde.getAlerteSoldeFaible())
                    .alerteSoldeEpuise(solde.getAlerteSoldeEpuise())
                    .messageAlerte(solde.getMessageAlerte())
                    .build();

        } catch (ArithmeticException e) {
            log.error("Division par zéro employe {}: {}", employe.getId(), e.getMessage());
            return buildErreur(employe, "Erreur calcul (division par zéro)");

        } catch (Exception e) {
            log.error("Erreur solde employe {}: {}", employe.getId(), e.getMessage());
            return buildErreur(employe, "Erreur: " + e.getMessage());
        }
    }

    private SoldeEmployeResumeDTO buildNonInitialise(Employe employe) {
        return SoldeEmployeResumeDTO.builder()
                .employeId(employe.getId())
                .matricule(employe.getMatricule())
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .totalJoursAcquis(BigDecimal.ZERO)
                .totalJoursConsommes(BigDecimal.ZERO)
                .soldeJoursDisponibles(BigDecimal.ZERO)
                .valeurEstimeeSolde(BigDecimal.ZERO)
                .montantTotalProvisionne(BigDecimal.ZERO)
                .montantTotalConsomme(BigDecimal.ZERO)
                .estInitialise(false)
                .alerteSoldeFaible(false)
                .alerteSoldeEpuise(false)
                .messageAlerte("⚠️ Non initialisé")
                .build();
    }

    private SoldeEmployeResumeDTO buildErreur(Employe employe, String msg) {
        return SoldeEmployeResumeDTO.builder()
                .employeId(employe.getId())
                .matricule(employe.getMatricule())
                .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                .totalJoursAcquis(BigDecimal.ZERO)
                .totalJoursConsommes(BigDecimal.ZERO)
                .soldeJoursDisponibles(BigDecimal.ZERO)
                .valeurEstimeeSolde(BigDecimal.ZERO)
                .montantTotalProvisionne(BigDecimal.ZERO)
                .montantTotalConsomme(BigDecimal.ZERO)
                .estInitialise(false)
                .alerteSoldeFaible(false)
                .alerteSoldeEpuise(false)
                .messageAlerte(msg)
                .build();
    }
}