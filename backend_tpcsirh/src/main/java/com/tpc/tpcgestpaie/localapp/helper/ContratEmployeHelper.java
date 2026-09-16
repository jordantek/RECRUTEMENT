package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContratEmployeHelper {

    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final NatureContratRepository natureContratRepository;
    private final ModeDePaiementRepository modeDePaiementRepository;
    private final BanqueRepository banqueRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    public ContratEmployeHelper(
            EmployeRepository employeRepository,
            CompanyRepository companyRepository,
            DepartementRepository departementRepository,
            PosteRepository posteRepository,
            CategorieEmployeRepository categorieEmployeRepository,
            NatureContratRepository natureContratRepository,
            ModeDePaiementRepository modeDePaiementRepository,
            BanqueRepository banqueRepository, ContratEmployeRepository contratEmployeRepository
    ) {
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
        this.departementRepository = departementRepository;
        this.posteRepository = posteRepository;
        this.categorieEmployeRepository = categorieEmployeRepository;
        this.natureContratRepository = natureContratRepository;
        this.modeDePaiementRepository = modeDePaiementRepository;
        this.banqueRepository = banqueRepository;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    public boolean isValid(ContratEmploye c) {
        return getInvalidFieldMessages(c).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(ContratEmploye c) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Employé
        if (c.getEmploye() == null || c.getEmploye().getId() == null) {
            errors.add(new ErrorResponse("employe", "L'employé est obligatoire"));
        } else if (!employeRepository.existsById(c.getEmploye().getId())) {
            errors.add(new ErrorResponse("employe_inexistant", "L'employé n'existe pas"));
        }

        // Entreprise
        if (c.getCompany() == null || c.getCompany().getId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire"));
        } else if (!companyRepository.existsById(c.getCompany().getId())) {
            errors.add(new ErrorResponse("company_inexistante", "L'entreprise n'existe pas"));
        }


        // Vérifier si un contrat existe déjà pour le même employé et la même entreprise
        if (c.getEmploye() != null && c.getEmploye().getId() != null &&
                c.getCompany() != null && c.getCompany().getId() != null) {
            boolean contratExiste = contratEmployeRepository.existsByEmployeIdAndCompanyId(c.getEmploye().getId(), c.getCompany().getId());

            if (contratExiste) {
                errors.add(new ErrorResponse("contrat_existant", "Un contrat existe déjà entre cet employé et cette entreprise"));
            }
        }
        // Département
        if (c.getDepartement() != null && c.getDepartement().getId() != null) {
            if (!departementRepository.existsById(c.getDepartement().getId())) {
                errors.add(new ErrorResponse("departement_inexistant", "Le département est inexistant"));
            }
        }

        // Poste
        if (c.getPoste() != null && c.getPoste().getId() != null) {
            if (!posteRepository.existsById(c.getPoste().getId())) {
                errors.add(new ErrorResponse("poste_inexistant", "Le poste est inexistant"));
            }
        }

        // Catégorie Employé
        if (c.getCategorieEmploye() != null && c.getCategorieEmploye().getId() != null) {
            if (!categorieEmployeRepository.existsById(c.getCategorieEmploye().getId())) {
                errors.add(new ErrorResponse("categorie_employe_inexistante", "La catégorie d'employé est inexistante"));
            }
        }

        // Nature Contrat
        if (c.getNatureContrat() == null || c.getNatureContrat().getId() == null) {
            errors.add(new ErrorResponse("nature_contrat", "La nature du contrat est obligatoire"));
        } else if (!natureContratRepository.existsById(c.getNatureContrat().getId())) {
            errors.add(new ErrorResponse("nature_contrat_inexistante", "La nature du contrat est inexistante"));
        }

        // Mode de paiement
        if (c.getModeDePaiement() == null || c.getModeDePaiement().getId() == null) {
            errors.add(new ErrorResponse("mode_paiement", "Le mode de paiement est obligatoire"));
        } else if (!modeDePaiementRepository.existsById(c.getModeDePaiement().getId())) {
            errors.add(new ErrorResponse("mode_paiement_inexistant", "Le mode de paiement est inexistant"));
        }

        // Banque
        if (c.getBanque() != null && c.getBanque().getId() != null) {
            if (!banqueRepository.existsById(c.getBanque().getId())) {
                errors.add(new ErrorResponse("banque_inexistante", "La banque est inexistante"));
            }
        }

        // Vérification enums : mouvement_contrat, type_contrat, duree_contrat
        if (c.getMouvement_contrat() == null) {
            errors.add(new ErrorResponse("mouvement_contrat", "Le mouvement du contrat est obligatoire"));
        } else if (isEnumInvalid(c.getMouvement_contrat(), GlobalEnums.MouvementContrat.class)) {
            errors.add(new ErrorResponse("mouvement_contrat_invalide", "Le mouvement de contrat est invalide. Valeurs autorisées : NOUVEAU, RENOUVELLEMENT"));
        }

        if (c.getType_contrat() == null) {
            errors.add(new ErrorResponse("type_contrat", "Le type de contrat est obligatoire"));
        } else if (isEnumInvalid(c.getType_contrat(), GlobalEnums.TypeContrat.class)) {
            errors.add(new ErrorResponse("type_contrat_invalide", "Le type de contrat est invalide. Valeurs autorisées: CDI, CDD"));
        }

        // Vérification dates
        if (c.getDate_debut() == null) {
            errors.add(new ErrorResponse("date_debut", "La date de début est obligatoire"));
        } else if (c.getDate_fin() != null && c.getDate_fin().isBefore(c.getDate_debut())) {
            errors.add(new ErrorResponse("date_fin_invalide", "La date de fin doit être postérieure à la date de début"));
        }

        if (c.getDebut_essai() != null && c.getFin_essai() != null && c.getFin_essai().isBefore(c.getDebut_essai())) {
            errors.add(new ErrorResponse("periode_essai_invalide", "La fin de la période d’essai doit être postérieure au début"));
        }

        // Numéro de compte requis si banque présente
        if (c.getBanque() != null && c.getBanque().getId() != null && (c.getNumero_compte() == null || c.getNumero_compte().isEmpty())) {
            errors.add(new ErrorResponse("numero_compte", "Le numéro de compte est obligatoire si une banque est sélectionnée"));
        }

        return errors;
    }


    public <E extends Enum<E>> boolean isEnumInvalid(String value, Class<E> enumClass) {
        try {
            Enum.valueOf(enumClass, value.toUpperCase());
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    public class GlobalEnums {
        public enum MouvementContrat {
            NOUVEAU, RENOUVELLEMENT, TRANSFERT
        }

        public enum TypeContrat {
            CDI, CDD, STAGE
        }

        public enum DureeContrat {
            DETERMINE, INDETERMINE, BESOIN
        }
    }

}
