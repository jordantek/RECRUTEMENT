package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.Mensualite;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.MensualiteRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class MensualiteHelper {

    private final CompanyRepository companyRepository;
    private final MensualiteRepository mensualiteRepository;

    public MensualiteHelper(CompanyRepository companyRepository,
                            MensualiteRepository mensualiteRepository) {
        this.companyRepository = companyRepository;
        this.mensualiteRepository = mensualiteRepository;
    }

    public boolean isValid(Mensualite mensualite) {
        return getInvalidFieldMessages(mensualite).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(Mensualite mensualite) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérif company
        if (mensualite.getCompany() == null || mensualite.getCompany().getId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire"));
        } else if (!companyRepository.existsById(mensualite.getCompany().getId())) {
            errors.add(new ErrorResponse("company_inexistante", "L'entreprise n'existe pas"));
        }

        // Vérif montant
        if (mensualite.getMontantMensue() == null) {
            errors.add(new ErrorResponse("montant", "Le montant est obligatoire"));
        } else if (mensualite.getMontantMensue().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ErrorResponse("montant_invalide", "Le montant doit être supérieur à zéro"));
        }

        // Vérif moisDebut
        if (mensualite.getMoisDemarrage() == null) {
            errors.add(new ErrorResponse("moisDebut", "Le mois de début est obligatoire"));
        }

        // Vérif moisFin
        if (mensualite.getMoisFin() == null) {
            errors.add(new ErrorResponse("moisFin", "Le mois de fin est obligatoire"));
        }

        // Vérif cohérence moisDebut <= moisFin
        if (mensualite.getMoisDemarrage() != null && mensualite.getMoisFin() != null) {
            YearMonth debut = YearMonth.parse(mensualite.getMoisDemarrage());
            YearMonth fin = YearMonth.parse(mensualite.getMoisFin());

            if (fin.isBefore(debut)) {
                errors.add(new ErrorResponse("periode_invalide", "Le mois de fin ne peut pas être antérieur au mois de début"));
            }
        }

        return errors;
    }
}
