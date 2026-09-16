package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.CongeProvisionCongeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisionManuelleService {

    private final CongeProvisionCongeRepository provisionRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;

    /**
     * Crée une provision manuellement pour un employé
     */
    @Transactional
    public ProvisionConge creerProvisionManuelle(Long employeId,
                                                 String moisReference,
                                                 BigDecimal salaireBrut,
                                                 BigDecimal joursTravailles,
                                                 BigDecimal joursAcquis) {

        log.info("Création manuelle provision: employe={}, mois={}, salaire={}, jours={}",
                employeId, moisReference, salaireBrut, joursAcquis);

        // Vérifications
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

        // Vérifier doublon
        if (provisionRepository.existsByEmployeIdAndMoisReference(employeId, moisReference)) {
            throw new RuntimeException("Provision déjà existante pour ce mois: " + moisReference);
        }

        // Récupérer company de l'employé
        Company company = Optional.ofNullable(employe.getCompany())
                .orElseThrow(() -> new RuntimeException("Employé sans entreprise"));

        // Calcul provision
        BigDecimal provisionMensuelle = salaireBrut
                .divide(joursTravailles, 4, RoundingMode.HALF_UP)
                .multiply(joursAcquis)
                .setScale(2, RoundingMode.HALF_UP);

        // Création
        ProvisionConge provision = new ProvisionConge();
        provision.setEmploye(employe);
        provision.setCompany(company);
        provision.setMoisReference(moisReference);
        provision.setJoursAcquis(joursAcquis);
        provision.setSalaireBrutMois(salaireBrut);
        provision.setJoursTravaillesMois(joursTravailles);
        provision.setProvisionMensuelle(provisionMensuelle);

        ProvisionConge sauvegardee = provisionRepository.save(provision);

        log.info("Provision créée: id={}, montant={}", sauvegardee.getId(), provisionMensuelle);

        return sauvegardee;
    }

    /**
     * Crée plusieurs provisions d'un coup (scénario de test)
     */
    @Transactional
    public void creerProvisionsTest(Long employeId, int nombreMois) {

        log.info("Création provisions test: employe={}, {} mois", employeId, nombreMois);

        YearMonth debut = YearMonth.of(2025, 3);
        BigDecimal salaireBase = new BigDecimal("173942");
        BigDecimal joursTravail = new BigDecimal("30");
        BigDecimal joursConge = new BigDecimal("2");

        for (int i = 0; i < nombreMois; i++) {
            YearMonth mois = debut.plusMonths(i);

            // Variation de salaire pour tester
            BigDecimal salaire = salaireBase.add(new BigDecimal(i * 200)); // 3000, 3200, 3400...

            try {
                creerProvisionManuelle(
                        employeId,
                        mois.toString(),
                        salaire,
                        joursTravail,
                        joursConge
                );
            } catch (RuntimeException e) {
                log.warn("Provision {} ignorée: {}", mois, e.getMessage());
            }
        }
    }

    /**
     * Liste toutes les provisions d'un employé
     */
    @Transactional(readOnly = true)
    public List<ProvisionConge> listerProvisions(Long employeId) {
        return provisionRepository.findByEmployeIdOrderByMoisReferenceAsc(employeId);
    }

    /**
     * Supprime une provision (si pas de consommation)
     */
    @Transactional
    public void supprimerProvision(Long provisionId) {
        ProvisionConge provision = provisionRepository.findById(provisionId)
                .orElseThrow(() -> new RuntimeException("Provision non trouvée"));

        if (provision.getJoursConsommes().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Impossible de supprimer: déjà consommée");
        }

        provisionRepository.delete(provision);
        log.info("Provision {} supprimée", provisionId);
    }

    /**
     * Réinitialise toutes les provisions d'un employé (DANGER - test uniquement)
     */
    @Transactional
    public void reinitialiserProvisions(Long employeId) {
        List<ProvisionConge> provisions = provisionRepository.findByEmployeIdOrderByMoisReferenceAsc(employeId);

        // Vérifier qu'aucune n'est consommée
        boolean hasConsommation = provisions.stream()
                .anyMatch(p -> p.getJoursConsommes().compareTo(BigDecimal.ZERO) > 0);

        if (hasConsommation) {
            throw new RuntimeException("Impossible: certaines provisions sont consommées");
        }

        provisionRepository.deleteAll(provisions);
        log.info("Réinitialisation complète pour employe {}", employeId);
    }
}