package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.MontantRubrique;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MontantRubriqueHelper {

    private final CompanyRepository companyRepository;
    private final RubriqueRepository rubriqueRepository;

    public MontantRubriqueHelper(CompanyRepository companyRepository,
                                 RubriqueRepository rubriqueRepository) {
        this.companyRepository = companyRepository;
        this.rubriqueRepository = rubriqueRepository;
    }

    public boolean isValid(MontantRubrique montantRubrique) {
        return getInvalidFieldMessages(montantRubrique).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(MontantRubrique montantRubrique) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérification de la société
        if (montantRubrique.getCompany() == null || montantRubrique.getCompany().getId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire"));
        } else if (!companyRepository.existsById(montantRubrique.getCompany().getId())) {
            errors.add(new ErrorResponse("company_inexistante", "L'entreprise n'existe pas"));
        }

        // Vérification de la rubrique
        if (montantRubrique.getRubrique() == null || montantRubrique.getRubrique().getId() == null) {
            errors.add(new ErrorResponse("rubrique", "La rubrique est obligatoire"));
        } else if (!rubriqueRepository.existsById(montantRubrique.getRubrique().getId())) {
            errors.add(new ErrorResponse("rubrique_inexistante", "La rubrique n'existe pas"));
        }

        // Vérification du montant
        if (montantRubrique.getMontantRubrique() == null) {
            errors.add(new ErrorResponse("montant", "Le montant est obligatoire"));
        } else if (montantRubrique.getMontantRubrique().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ErrorResponse("montant_invalide", "Le montant ne peut pas être négatif"));
        }

        return errors;
    }
}
