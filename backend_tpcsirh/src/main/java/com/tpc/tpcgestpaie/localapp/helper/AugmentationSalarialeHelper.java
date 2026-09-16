package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.AugmentationSalariale;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AugmentationSalarialeRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AugmentationSalarialeHelper {

    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;
    private final AugmentationSalarialeRepository augmentationSalarialeRepository;

    public AugmentationSalarialeHelper(ContratEmployeRepository contratEmployeRepository, CompanyRepository companyRepository, EmployeRepository employeRepository, AugmentationSalarialeRepository augmentationSalarialeRepository) {
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
        this.augmentationSalarialeRepository = augmentationSalarialeRepository;
    }

    public boolean isValid(AugmentationSalariale entity) {
        return getInvalidFieldMessages(entity).isEmpty();
    }

    public boolean isDuplicateAugmentation(Long employeId, LocalDate dateEffet, String motif) {
        return augmentationSalarialeRepository.existsByEmployeIdAndDateEffetAndMotifAugmentation(employeId, dateEffet, motif);
    }

    public List<ErrorResponse> getInvalidFieldMessages(AugmentationSalariale entity) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Date d'effet obligatoire
        if (entity.getDateEffet() == null) {
            errors.add(new ErrorResponse("date_effet", "La date d'effet est obligatoire"));
        }

        // Pas de validation de montants ici, elles se font dans AugmentationRubriqueHelper

        return errors;
    }
}
