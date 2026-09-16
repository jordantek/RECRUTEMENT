package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.EmployeDiplome;
import com.tpc.tpcgestpaie.localapp.repository.DiplomeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeDiplomeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeDiplomeHelper {

    private final EmployeRepository employeRepository;
    private final DiplomeRepository diplomeRepository;
    private final EmployeDiplomeRepository employeDiplomeRepository;

    public EmployeDiplomeHelper(
            EmployeRepository employeRepository,
            DiplomeRepository diplomeRepository,
            EmployeDiplomeRepository employeDiplomeRepository
    ) {
        this.employeRepository = employeRepository;
        this.diplomeRepository = diplomeRepository;
        this.employeDiplomeRepository = employeDiplomeRepository;
    }

    public boolean isValid(EmployeDiplome ed) {
        return getInvalidFieldMessages(ed).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(EmployeDiplome ed) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérification de l'employé
        if (ed.getEmploye() == null || ed.getEmploye().getId() == null) {
            errors.add(new ErrorResponse("employe", "L'employé est obligatoire"));
        } else if (!employeRepository.existsById(ed.getEmploye().getId())) {
            errors.add(new ErrorResponse("employe_inexistant", "L'employé n'existe pas"));
        }

        // Vérification du diplôme
        if (ed.getDiplome() == null || ed.getDiplome().getId() == null) {
            errors.add(new ErrorResponse("diplome", "Le diplôme est obligatoire"));
        } else if (!diplomeRepository.existsById(ed.getDiplome().getId())) {
            errors.add(new ErrorResponse("diplome_inexistant", "Le diplôme n'existe pas"));
        }

        // Vérification de la date d'obtention
        if (ed.getAnnee_obtention() == null) {
            errors.add(new ErrorResponse("date_obtention", "La date d’obtention du diplôme est obligatoire"));
        }

        if (ed.getDenomination() == null) {
            errors.add(new ErrorResponse("denomination", "La dénomination du diplôme est obligatoire"));
        }

        // Unicité : un même diplôme ne doit pas être associé plusieurs fois à un employé
        if (ed.getEmploye() != null && ed.getEmploye().getId() != null &&
                ed.getDiplome() != null && ed.getDiplome().getId() != null) {

            boolean existe = employeDiplomeRepository
                    .existsByEmployeIdAndDiplomeId(ed.getEmploye().getId(), ed.getDiplome().getId());

            if (existe) {
                errors.add(new ErrorResponse("association_existante", "Ce diplôme est déjà associé à cet employé"));
            }
        }

        return errors;
    }
}
