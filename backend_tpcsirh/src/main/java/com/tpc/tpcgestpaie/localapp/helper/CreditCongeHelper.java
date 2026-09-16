package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.CreditConge;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreditCongeHelper {

    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CreditCongeRepository creditCongeRepository;

    public CreditCongeHelper(
            EmployeRepository employeRepository,
            CompanyRepository companyRepository,
            ContratEmployeRepository contratEmployeRepository,
            CreditCongeRepository creditCongeRepository
    ) {
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.creditCongeRepository = creditCongeRepository;
    }

    public boolean isValid(CreditConge creditConge) {
        return getInvalidFieldMessages(creditConge).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(CreditConge creditConge) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérif employé
        if (creditConge.getEmploye() == null || creditConge.getEmploye().getId() == null) {
            errors.add(new ErrorResponse("employe", "L'employé est obligatoire"));
        } else if (!employeRepository.existsById(creditConge.getEmploye().getId())) {
            errors.add(new ErrorResponse("employe_inexistant", "L'employé n'existe pas"));
        }

        // Vérif contratEmploye
        if (creditConge.getContratEmploye() == null || creditConge.getContratEmploye().getId() == null) {
            errors.add(new ErrorResponse("contrat_employe", "Le contrat employé est obligatoire"));
        } else if (!contratEmployeRepository.existsById(creditConge.getContratEmploye().getId())) {
            errors.add(new ErrorResponse("contrat_employe_inexistant", "Le contrat employé n'existe pas"));
        }

        // Vérif company
        if (creditConge.getCompany() == null || creditConge.getCompany().getId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire"));
        } else if (!companyRepository.existsById(creditConge.getCompany().getId())) {
            errors.add(new ErrorResponse("company_inexistante", "L'entreprise n'existe pas"));
        }

        // Vérif dateReference
        if (creditConge.getDateReference() == null) {
            errors.add(new ErrorResponse("date_reference", "La date de référence est obligatoire"));
        } else if (creditConge.getDateReference().isAfter(LocalDate.now())) {
            errors.add(new ErrorResponse("date_reference_invalide", "La date de référence ne peut pas être dans le futur"));
        }


        return errors;
    }
}
