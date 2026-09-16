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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisionSalaireService {

    private final CongeProvisionCongeRepository provisionRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;

    /**
     * Génère ou met à jour la provision d'un employé lors de la validation de sa paie
     * Appelé par ton service de paie après validation de la fiche de paie mensuelle
     *
     * @param employeId ID de l'employé
     * @param moisPaie Mois de paie (ex: "2025-02" pour février 2025)
     * @param salaireBrut Salaire brut de la fiche de paie (obligatoire)
     * @param joursTravailles Jours travaillés selon la paie (prorata si entrée/sortie)
     * @return La provision créée ou mise à jour
     */
    @Transactional
        public ProvisionConge genererProvisionFromPaie(Long employeId,
                                                   String moisPaie,
                                                   BigDecimal salaireBrut,
                                                   BigDecimal joursTravailles) {

        // Vérifications
        if (salaireBrut == null || salaireBrut.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salaire brut invalide: " + salaireBrut);
        }
        // Récupération employé et entreprise
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé: " + employeId));

        Company company = Optional.ofNullable(employe.getCompany())
                .orElseThrow(() -> new RuntimeException("Employé sans entreprise: " + employeId));

        // Vérifier si provision existe déjà
        Optional<ProvisionConge> existante = provisionRepository
                .findByEmployeIdAndMoisReference(employeId, moisPaie);

        if (existante.isPresent()) {
           return mettreAJourProvision(existante.get(), salaireBrut, joursTravailles);
        }

        // Calcul des jours de congé acquis selon config entreprise
        BigDecimal joursAcquis = calculerJoursAcquis(company, joursTravailles);

        // Calcul de la provision
        BigDecimal provisionMensuelle = salaireBrut
                .divide(joursTravailles, 4, RoundingMode.HALF_UP)
                .multiply(joursAcquis)
                .setScale(2, RoundingMode.HALF_UP);

        // Création
        ProvisionConge provision = new ProvisionConge();
        provision.setEmploye(employe);
        provision.setCompany(company);
        provision.setMoisReference(moisPaie);
        provision.setJoursAcquis(joursAcquis);
        provision.setSalaireBrutMois(salaireBrut);
        provision.setJoursTravaillesMois(joursTravailles);
        provision.setProvisionMensuelle(provisionMensuelle);

        ProvisionConge sauvegardee = provisionRepository.save(provision);
        return sauvegardee;
    }

    /**
     * Met à jour une provision existante (si correction de paie)
     */
    @Transactional
    public ProvisionConge mettreAJourProvision(ProvisionConge provision,
                                               BigDecimal nouveauSalaire,
                                               BigDecimal nouveauxJoursTravailles) {

        // Vérifier qu'elle n'est pas déjà consommée
        if (provision.getJoursConsommes().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Impossible de modifier: provision déjà consommée ("
                    + provision.getJoursConsommes() + " jours)");
        }

        // Recalcul
        BigDecimal joursAcquis = calculerJoursAcquis(provision.getCompany(), nouveauxJoursTravailles);

        BigDecimal provisionMensuelle = nouveauSalaire
                .divide(nouveauxJoursTravailles, 4, RoundingMode.HALF_UP)
                .multiply(joursAcquis)
                .setScale(2, RoundingMode.HALF_UP);

        provision.setSalaireBrutMois(nouveauSalaire);
        provision.setJoursTravaillesMois(nouveauxJoursTravailles);
        provision.setJoursAcquis(joursAcquis);
        provision.setProvisionMensuelle(provisionMensuelle);

        ProvisionConge miseAJour = provisionRepository.save(provision);

        return miseAJour;
    }

    /**
     * Génère les provisions pour TOUS les employés d'une paie mensuelle
     * Appelé après validation de la paie de l'entreprise
     */
    @Transactional
    public void genererProvisionsMoisPaie(Long companyId,
                                          String moisPaie,
                                          java.util.function.Function<Long, PaieData> paieProvider) {

    }

    // ============================================
    // MÉTHODES UTILITAIRES
    // ============================================

    private BigDecimal calculerJoursAcquis(Company company, BigDecimal joursTravailles) {
        Double nbrJourConge = company.getNbrJourConge();  // 2.0 ou 2.5

        if (nbrJourConge == null) {
            throw new RuntimeException("Configuration congés incomplète pour l'entreprise");
        }

        // Tout le monde a 2.5 jours (ou 2.0) par mois, point final
        return BigDecimal.valueOf(nbrJourConge);
    }

    // ============================================
    // DTO INTERNE
    // ============================================

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PaieData {
        private BigDecimal salaireBrut;
        private BigDecimal joursTravailles;
    }
}