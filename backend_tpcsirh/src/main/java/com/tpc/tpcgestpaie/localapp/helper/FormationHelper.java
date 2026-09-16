package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.dto.administration.FormationDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.FormationRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FormationHelper {

    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;
    private final FormationRepository formationRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    public FormationHelper(
            CompanyRepository companyRepository,
            EmployeRepository employeRepository, FormationRepository formationRepository, ContratEmployeRepository contratEmployeRepository
    ) {
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
        this.formationRepository = formationRepository;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    public boolean isValid(FormationDTO formation) {
        return getInvalidFieldMessages(formation).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(FormationDTO formation) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérification du thème
        if (formation.getTheme() == null || formation.getTheme().trim().isEmpty()) {
            errors.add(new ErrorResponse("theme", "Le thème de la formation est obligatoire"));
        }

        // Vérification du lieu
        if (formation.getLieu() == null || formation.getLieu().trim().isEmpty()) {
            errors.add(new ErrorResponse("lieu", "Le lieu de la formation est obligatoire"));
        }

        // Vérification de la date de début
        if (formation.getDateDebut() == null) {
            errors.add(new ErrorResponse("date_debut", "La date de début est obligatoire"));
        }

        // Vérification de la date de fin
        if (formation.getDateFin() == null) {
            errors.add(new ErrorResponse("date_fin", "La date de fin est obligatoire"));
        }

        if (formation.getDateDebut() != null && formation.getDateFin() != null) {
            if (formation.getDateFin().isBefore(formation.getDateDebut())) {
                errors.add(new ErrorResponse("date_fin", "La date de fin ne peut pas être antérieure à la date de début"));
            }
        }

        if (formation.getCompanyId() == null || !companyRepository.existsById(formation.getCompanyId())) {
            errors.add(new ErrorResponse("companyId", "Entreprise introuvable"));
        }
        if (formation.getCompanyId() == null || !companyRepository.existsById(formation.getCompanyId())) {
            errors.add(new ErrorResponse("companyId", "Entreprise introuvable"));
        }
        // Vérifier si company est null
        boolean themeExists = formationRepository.existsByCompanyIdAndTheme(
                formation.getCompanyId(),
                formation.getTheme(),
                formation.getId()
        );
        if (themeExists) {
            errors.add(new ErrorResponse("theme_existant", "Une formation avec ce thème existe déjà pour cette entreprise"));
        }

        // Vérification des employés associés
        List<Employe> employes = employeRepository.findAllById(formation.getEmployeIds());

        Set<Long> foundIds = employes.stream()
                .map(Employe::getId)
                .collect(Collectors.toSet());

        for (Long id : formation.getEmployeIds()) {
            if (!foundIds.contains(id)) {
                errors.add(new ErrorResponse("employe_inexistant", "L'employé ID " + id + " n'existe pas"));
            } else {
                boolean contratExiste = contratEmployeRepository.existsByEmployeIdAndCompanyId(id, formation.getCompanyId());
                if (!contratExiste) {
                    errors.add(new ErrorResponse(
                            "contrat_inexistant",
                            "L'employé ID " + id + " n'a pas de contrat avec l'entreprise associée à la formation"
                    ));
                }
            }
        }

        return errors;
    }


}
