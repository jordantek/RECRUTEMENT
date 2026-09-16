package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.ContratEmployeRubrique;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContratEmployeRubriqueHelper {

    private final ContratEmployeRepository contratEmployeRepository;
    private final RubriqueRepository rubriqueRepository;

    public ContratEmployeRubriqueHelper(
            ContratEmployeRepository contratEmployeRepository,
            RubriqueRepository rubriqueRepository
    ) {
        this.contratEmployeRepository = contratEmployeRepository;
        this.rubriqueRepository = rubriqueRepository;
    }

    public boolean isValid(ContratEmployeRubrique entity) {
        return getInvalidFieldMessages(entity).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(ContratEmployeRubrique entity) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Contrat Employé obligatoire et doit exister
//        if (entity.getContratEmploye() == null || entity.getContratEmploye().getId() == null) {
//            errors.add(new ErrorResponse("contrat_employe", "Le contrat employé est obligatoire"));
//        } else if (!contratEmployeRepository.existsById(entity.getContratEmploye().getId())) {
//            errors.add(new ErrorResponse("contrat_employe_inexistant", "Le contrat employé n'existe pas"));
//        }

        // Rubrique obligatoire et doit exister
        if (entity.getRubrique() == null || entity.getRubrique().getId() == null) {
            errors.add(new ErrorResponse("rubrique", "La rubrique est obligatoire"));
        } else if (!rubriqueRepository.existsById(entity.getRubrique().getId())) {
            errors.add(new ErrorResponse("rubrique_inexistante", "La rubrique n'existe pas"));
        }

        // Montant obligatoire et >= 0
        if (entity.getMontant() == null) {
            errors.add(new ErrorResponse("montant", "Le montant est obligatoire"));
        } else if (entity.getMontant().doubleValue() < 0) {
            errors.add(new ErrorResponse("montant_invalide", "Le montant doit être supérieur ou égal à zéro"));
        }

        // Statut obligatoire et doit être une valeur valide (ACTIF ou INACTIF)
        if (entity.getStatut() == null) {
            errors.add(new ErrorResponse("statut", "Le statut est obligatoire"));
        } else {
            boolean statutValide = false;
            for (ContratEmployeRubrique.Statut s : ContratEmployeRubrique.Statut.values()) {
                if (s == entity.getStatut()) {
                    statutValide = true;
                    break;
                }
            }
            if (!statutValide) {
                errors.add(new ErrorResponse("statut_invalide", "Le statut est invalide"));
            }
        }

        // Dates (décommenter et adapter si besoin)
    /*
    if (entity.getDateDebut() == null) {
        errors.add(new ErrorResponse("date_debut", "La date de début est obligatoire"));
    }
    if (entity.getDateFin() != null && entity.getDateDebut() != null &&
            entity.getDateFin().isBefore(entity.getDateDebut())) {
        errors.add(new ErrorResponse("date_fin_invalide", "La date de fin doit être postérieure à la date de début"));
    }
    */

        return errors;
    }

}
