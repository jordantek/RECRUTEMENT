package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.CalculUtils.AncienneteCalculator;
import com.tpc.tpcgestpaie.localapp.dto.PrimeAncienneteResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.anciennete.AncienneteSettingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrimeAncienneteService {

    private final AncienneteSettingService ancienneteSettingService;
    private final ContratEmployeService contratEmployeService;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;
    private final AncienneteCalculator ancienneteCalculator;
    private final ContratEmployeRepository contratEmployeRepository;

    public PrimeAncienneteService(
            AncienneteSettingService ancienneteSettingService,
            ContratEmployeService contratEmployeService,
            CompanyService companyService,
            EmployeService employeService, ContratEmployeRubriqueService contratEmployeRubriqueService, AncienneteCalculator ancienneteCalculator, ContratEmployeRepository contratEmployeRepository) {
        this.ancienneteSettingService = ancienneteSettingService;
        this.contratEmployeService = contratEmployeService;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
        this.ancienneteCalculator = ancienneteCalculator;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    /**
     * Calcule la prime d'ancienneté pour un employé donné
     */
    public PrimeAncienneteResponseDTO calculerPrimeAnciennete(Long companyId, Long employeId) {
        try {
            // 1. Vérifier si la prime d'ancienneté est activée pour l'entreprise
            boolean enabled = ancienneteSettingService.isAncienneteEnabledForCompany(companyId);

            if (!enabled) {
                return new PrimeAncienneteResponseDTO("La prime d'ancienneté n'est pas activée pour cette entreprise");
            }

            // Si employeId est null, on retourne juste l'état activé
            if (employeId == null) {
                return new PrimeAncienneteResponseDTO(true, 0.0, 0.0, 0);
            }

            // 2. Vérifier que l'entreprise existe
            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return new PrimeAncienneteResponseDTO("Entreprise non trouvée");
            }

            // 3. Vérifier que l'employé existe
            Optional<Employe> employeOpt = employeService.findById(employeId);
            if (employeOpt.isEmpty()) {
                return new PrimeAncienneteResponseDTO("Employé non trouvé");
            }

            Employe employe = employeOpt.get();

            // 4. Récupérer les contrats valides de l'employé
            List<ContractEmployeDTO> contrats = contratEmployeService
                    .getAllContratEmployeEnCoursDeValiditeParEntreprise(companyId);

            // Trouver le contrat actif de cet employé
            Optional<ContractEmployeDTO> contratActifOpt = contrats.stream()
                    .filter(c -> c.getEmployeId() != null && c.getEmployeId().equals(employeId))
                    .findFirst();

            if (contratActifOpt.isEmpty()) {
                return new PrimeAncienneteResponseDTO("Aucun contrat actif trouvé pour cet employé");
            }
            ContractEmployeDTO contratActif = contratActifOpt.get();
            // 5. Calculer l'ancienneté
            double ancienneteEnAnnees = calculerAnciennete(employeId,companyId);
            // 6. Calculer la prime d'ancienneté
            List<ContratEmployeRubriqueDTO> elementContract = contratEmployeRubriqueService.getByCompanyId(companyOpt.get().getId());
            double salaireBasecontrat = elementContract.stream()
                    .filter(ec -> ec.getContratEmployeId() != null
                            && ec.getContratEmployeId().equals(contratActif.getId())
                            && ec.getLibelle() != null
                            && ec.getLibelle().trim().equalsIgnoreCase("SALAIRE DE BASE"))
                    .mapToDouble(ec -> {
                        double montant = ec.getMontant() != null ? ec.getMontant().doubleValue() : 0.0;
                        double montantAjouter = ec.getMontant_ajouter() != null ? ec.getMontant_ajouter().doubleValue() : 0.0;
                        return montant + montantAjouter;
                    })
                    .findFirst()
                    .orElse(0.0);

            Double primeAnciennete = calculerPrimeSelonAnciennete(
                    (int) ancienneteEnAnnees,
                    salaireBasecontrat
            );
            return new PrimeAncienneteResponseDTO(
                    true,
                    primeAnciennete,
                    salaireBasecontrat,
                    (int) ancienneteEnAnnees
            );

        } catch (Exception e) {
            return new PrimeAncienneteResponseDTO("Erreur lors du calcul: " + e.getMessage());
        }
    }

    /**
     * Calcule l'ancienneté en années à partir de la date d'embauche
     */
//    private Integer calculerAnciennete(ContractEmployeDTO contrat) {
//        if (contrat.getDateEmbauche() == null) {
//            return 0;
//        }
//
//        Period period = Period.between(contrat.getDateEmbauche(), LocalDate.now());
//        return period.getYears();
//    }

    public int calculerAnciennete(Long employeId, Long entrepriseId) {
        // Vérifier si le paramètre d'ancienneté existe pour l'entreprise
        if (!ancienneteSettingService.getByCompanyId(entrepriseId).isPresent()) {
            System.err.println("❌ Paramètre d'ancienneté non trouvé pour l'entreprise ID: " + entrepriseId);
            return (int) 0.0;
        }

        // Récupérer l'écart en mois configuré pour l'entreprise
        Integer ecart = ancienneteSettingService.getEcartMoisForCompany(entrepriseId);

        if (ecart == null) {
            System.err.println("❌ Écart mois non configuré pour l'entreprise ID: " + entrepriseId);
            return (int) 0.0;
        }

        // Récupérer tous les contrats de l'employé dans cette entreprise
        List<ContratEmploye> contrats = contratEmployeRepository
                .findByEmployeIdAndCompanyId(employeId, entrepriseId);
        return (int) ancienneteCalculator.calculerAnciennete(contrats, ecart);
    }

    /**
     * Calcule la prime selon les règles d'ancienneté
     */
    private Double calculerPrimeSelonAnciennete(Integer ancienneteAnnees, Double salaireBase) {
        // Debug logging
        System.out.println("DEBUG - ancienneteAnnees: " + ancienneteAnnees);
        System.out.println("DEBUG - salaireBase: " + salaireBase);
        System.out.println("DEBUG - Type ancienneteAnnees: " + (ancienneteAnnees != null ? ancienneteAnnees.getClass() : "null"));

        if (ancienneteAnnees == null || salaireBase == null) {
            System.out.println("DEBUG - Une des valeurs est null");
            return 0.0;
        }

        System.out.println("DEBUG - Condition 1 (3-5 ans): " + (ancienneteAnnees >= 3 && ancienneteAnnees < 5));

        if (ancienneteAnnees >= 3 && ancienneteAnnees < 5) {
            double prime = salaireBase * 0.03;
            System.out.println("DEBUG - Prime calculée: " + prime);
            return prime;
        } else if (ancienneteAnnees >= 5 && ancienneteAnnees < 7) {
            // 5% après 5 ans de présence
            return salaireBase * 0.05;
        } else if (ancienneteAnnees >= 7 && ancienneteAnnees <= 20) {
            // 5% de base (acquis à 5 ans) + 1% par année supplémentaire de la 7ème à la 20ème année
            double pourcentageBase = 0.05;
            double anneesSupplementaires = ancienneteAnnees - 5;
            double pourcentageSupplementaire = anneesSupplementaires * 0.01;

            double pourcentageTotal = pourcentageBase + pourcentageSupplementaire;
            return salaireBase * pourcentageTotal;
        } else if (ancienneteAnnees > 20) {
            // Maximum atteint à 20 ans : 5% + 15% = 20%
            return salaireBase * 0.20;
        }

        return 0.0;
    }
}