package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.PrelevementMensualite;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.MensualiteRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PrelevementMensualiteHelper {

    private final CompanyRepository companyRepository;
    private final MensualiteRepository mensualiteRepository;
    private final InstitutionRepository institutionRepository;

    public PrelevementMensualiteHelper(CompanyRepository companyRepository,
                                       MensualiteRepository mensualiteRepository,
                                       InstitutionRepository institutionRepository) {
        this.companyRepository = companyRepository;
        this.mensualiteRepository = mensualiteRepository;
        this.institutionRepository = institutionRepository;
    }

    public boolean isValid(PrelevementMensualite prelevement) {
        return getInvalidFieldMessages(prelevement).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(PrelevementMensualite prelevement) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérif company
        if (prelevement.getCompany() == null || prelevement.getCompany().getId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire"));
        } else if (!companyRepository.existsById(prelevement.getCompany().getId())) {
            errors.add(new ErrorResponse("company_inexistante", "L'entreprise n'existe pas"));
        }

        // Vérif institution
        if (prelevement.getInstitution() == null || prelevement.getInstitution().getId() == null) {
            errors.add(new ErrorResponse("institution", "L'institution est obligatoire"));
        } else if (!institutionRepository.existsById(prelevement.getInstitution().getId())) {
            errors.add(new ErrorResponse("institution_inexistante", "L'institution n'existe pas"));
        }

        // Vérif mensualite
        if (prelevement.getMensualite() == null || prelevement.getMensualite().getId() == null) {
            errors.add(new ErrorResponse("mensualite", "La mensualité est obligatoire"));
        } else if (!mensualiteRepository.existsById(prelevement.getMensualite().getId())) {
            errors.add(new ErrorResponse("mensualite_inexistante", "La mensualité n'existe pas"));
        }

        // Vérif montant
        if (prelevement.getMontant() == null) {
            errors.add(new ErrorResponse("montant", "Le montant est obligatoire"));
        } else if (prelevement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ErrorResponse("montant_invalide", "Le montant doit être supérieur à zéro"));
        }

        // Vérif date prélèvement
        if (prelevement.getDatePrelevement() == null) {
            errors.add(new ErrorResponse("datePrelevement", "La date de prélèvement est obligatoire"));
        } else if (prelevement.getDatePrelevement().isAfter(LocalDate.now())) {
            errors.add(new ErrorResponse("datePrelevement_future", "La date de prélèvement ne peut pas être dans le futur"));
        }

        // Vérif mois prélèvement
        if (prelevement.getMoisPrelevement() == null || prelevement.getMoisPrelevement().isEmpty()) {
            errors.add(new ErrorResponse("moisPrelevement", "Le mois de prélèvement est obligatoire"));
        }

        return errors;
    }
}
