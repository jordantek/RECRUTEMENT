package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.dto.administration.SanctionDTO;
import com.tpc.tpcgestpaie.localapp.repository.administration.SanctionRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class SanctionHelper {

    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SanctionRepository sanctionRepository;

    public SanctionHelper(EmployeRepository employeRepository, ContratEmployeRepository contratRepository, CompanyRepository companyRepository, UserRepository userRepository, SanctionRepository sanctionRepository) {
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.sanctionRepository = sanctionRepository;
    }

    public List<ErrorResponse> getInvalidFieldMessages(SanctionDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (dto.getEmployeId() == null || !employeRepository.existsById(dto.getEmployeId())) {
            errors.add(new ErrorResponse("employeId", "Employé introuvable"));
        }

        if (dto.getContratEmployeId() == null || !contratRepository.existsById(dto.getContratEmployeId())) {
            errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable"));
        }

        if (dto.getCompanyId() == null || !companyRepository.existsById(dto.getCompanyId())) {
            errors.add(new ErrorResponse("companyId", "Entreprise introuvable"));
        }

        if (dto.getDatePlainte() == null) {
            errors.add(new ErrorResponse("datePlainte", "La date de la plainte est obligatoire"));
        }

        if (dto.getContenuePlainte() == null || dto.getContenuePlainte().trim().isEmpty()) {
            errors.add(new ErrorResponse("contenuePlainte", "Le contenu de la plainte est obligatoire"));
        }

        // Validations de cohérence des dates
        if (dto.getDatePlainte() != null) {
            LocalDate aujourdhui = LocalDate.now();

            // datePlainte doit être antérieure ou égale à aujourd'hui
            if (dto.getDatePlainte().isAfter(aujourdhui)) {
                errors.add(new ErrorResponse("datePlainte", "La date de la plainte ne peut pas être dans le futur"));
            }
        }

        if (dto.getDatePlainte() != null && dto.getDateDemandeExplication() != null) {
            // dateDemandeExplication ne doit pas être antérieure à datePlainte
            if (dto.getDateDemandeExplication().isBefore(dto.getDatePlainte())) {
                errors.add(new ErrorResponse("dateDemandeExplication", "La date de demande d'explication ne peut pas être antérieure à la date de plainte"));
            }
        }

        if (dto.getDateDemandeExplication() != null && dto.getDateReponse() != null) {
            // dateReponse ne doit pas être antérieure à dateDemandeExplication
            if (dto.getDateReponse().isBefore(dto.getDateDemandeExplication())) {
                errors.add(new ErrorResponse("dateReponse", "La date de réponse ne peut pas être antérieure à la date de demande d'explication"));
            }
        }

        // Validation optionnelle : dateReponse ne doit pas être antérieure à datePlainte
        if (dto.getDatePlainte() != null && dto.getDateReponse() != null) {
            if (dto.getDateReponse().isBefore(dto.getDatePlainte())) {
                errors.add(new ErrorResponse("dateReponse", "La date de réponse ne peut pas être antérieure à la date de plainte"));
            }
        }

        // Vérifie s'il existe déjà une plainte identique (commenté pour l'instant)
    /* if (dto.getEmployeId() != null && dto.getDatePlainte() != null && dto.getContenuePlainte() != null) {
        List<Sanction> existing = sanctionRepository.findByEmployeIdAndDatePlainteAndContenuePlainte(
                dto.getEmployeId(),
                dto.getDatePlainte(),
                dto.getContenuePlainte()
        );

        if (!existing.isEmpty() && (dto.getId() == null || existing.stream().noneMatch(s -> s.getId().equals(dto.getId())))) {
            errors.add(new ErrorResponse("contenuePlainte", "Une plainte identique existe déjà pour cet employé à cette date"));
        }
    } */

        return errors;
    }
    public List<ErrorResponse> getInvalidUpdateFieldMessages(SanctionDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (dto.getDatePlainte() == null) {
            errors.add(new ErrorResponse("datePlainte", "La date de la plainte est obligatoire"));
        }

        if (dto.getContenuePlainte() == null || dto.getContenuePlainte().trim().isEmpty()) {
            errors.add(new ErrorResponse("contenuePlainte", "Le contenu de la plainte est obligatoire"));
        }

        return errors;
    }
}
