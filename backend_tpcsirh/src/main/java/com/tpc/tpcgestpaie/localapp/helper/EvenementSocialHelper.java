package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.dto.administration.EvenementSocialDTO;
import com.tpc.tpcgestpaie.localapp.model.EvenementSocial;
import com.tpc.tpcgestpaie.localapp.repository.administration.EvenementSocialRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EvenementSocialHelper {

    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final EvenementSocialRepository evenementSocialRepository;

    public EvenementSocialHelper(EmployeRepository employeRepository, ContratEmployeRepository contratRepository, CompanyRepository companyRepository, UserRepository userRepository, EvenementSocialRepository evenementSocialRepository) {
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.evenementSocialRepository = evenementSocialRepository;
    }

    public List<ErrorResponse> getInvalidFieldMessages(EvenementSocialDTO dto) {
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

        if (dto.getDateEvenement() == null) {
            errors.add(new ErrorResponse("dateEvenement", "La date de l'événement est obligatoire"));
        }

        if (dto.getDesignation() == null || dto.getDesignation().trim().isEmpty()) {
            errors.add(new ErrorResponse("designation", "La désignation de l'événement est obligatoire"));
        }

        // Vérifie s'il existe déjà un événement identique
        if (dto.getEmployeId() != null && dto.getDateEvenement() != null && dto.getDesignation() != null) {
            // Utiliser Pageable.unpaged() pour récupérer tous les résultats sans pagination
            Page<EvenementSocial> existingPage = evenementSocialRepository.findByEmployeIdAndDateEvenementAndDesignation(
                    dto.getEmployeId(),
                    dto.getDateEvenement(),
                    dto.getDesignation(),
                    Pageable.unpaged() // Correction ici
            );

            List<EvenementSocial> existing = existingPage.getContent();

            if (!existing.isEmpty() && (dto.getId() == null || existing.stream().noneMatch(e -> e.getId().equals(dto.getId())))) {
                errors.add(new ErrorResponse("designation", "Un événement identique existe déjà pour cet employé à cette date"));
            }
        }

        return errors;
    }
    public List<ErrorResponse> getInvalidUpdateFieldMessages(EvenementSocialDTO dto) {
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

        if (dto.getDateEvenement() == null) {
            errors.add(new ErrorResponse("dateEvenement", "La date de l'événement est obligatoire"));
        }

        if (dto.getDesignation() == null || dto.getDesignation().trim().isEmpty()) {
            errors.add(new ErrorResponse("designation", "La désignation de l'événement est obligatoire"));
        }

        return errors;
    }
}
