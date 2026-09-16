package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.dto.administration.AbsenceDTO;
import com.tpc.tpcgestpaie.localapp.model.Absence;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.MotifAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.TypeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AbsenceHelper {

    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratRepository;
    private final CompanyRepository companyRepository;
    private final MotifAbsenceRepository motifRepository;
    private final CreditCongeRepository creditRepository;
    private final TypeAbsenceRepository typeAbsenceRepository;
    private final AbsenceRepository absenceRepository;

    public AbsenceHelper(EmployeRepository employeRepository, ContratEmployeRepository contratRepository, CompanyRepository companyRepository, MotifAbsenceRepository motifRepository, CreditCongeRepository creditRepository, TypeAbsenceRepository typeAbsenceRepository, AbsenceRepository absenceRepository) {
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.companyRepository = companyRepository;
        this.motifRepository = motifRepository;
        this.creditRepository = creditRepository;
        this.typeAbsenceRepository = typeAbsenceRepository;
        this.absenceRepository = absenceRepository;
    }


    public List<ErrorResponse> getInvalidFieldMessages(AbsenceDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Vérification contrat
        if (dto.getContratEmployeId() == null || !contratRepository.existsById(dto.getContratEmployeId())) {
            errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable"));
        }

        // Vérification type d'absence
        if (dto.getTypeAbsenceId() == null || !typeAbsenceRepository.existsById(dto.getTypeAbsenceId())) {
            errors.add(new ErrorResponse("typeAbsenceId", "Type d’absence introuvable"));
        }

        LocalDate dateDebut = null;
        LocalDate dateFin = null;

        // Vérifier dateDebut
        if (dto.getDateDebut() == null) {
            errors.add(new ErrorResponse("dateDebut", "La date de début est obligatoire"));
        } else {
            try {
                dateDebut = LocalDate.parse(dto.getDateDebut().toString(), formatter);
            } catch (DateTimeParseException e) {
                errors.add(new ErrorResponse("dateDebut", "Format invalide. Format attendu : yyyy-MM-dd"));
            }
        }

        // Vérifier dateFin
        if (dto.getDateFin() == null) {
            errors.add(new ErrorResponse("dateFin", "La date de fin est obligatoire"));
        } else {
            try {
                dateFin = LocalDate.parse(dto.getDateFin().toString(), formatter);
            } catch (DateTimeParseException e) {
                errors.add(new ErrorResponse("dateFin", "Format invalide. Format attendu : yyyy-MM-dd"));
            }
        }

        // Vérifier cohérence des dates (si valides)
        if (dateDebut != null && dateFin != null && dateDebut.isAfter(dateFin)) {
            errors.add(new ErrorResponse("dateFin", "La date de fin doit être postérieure à la date de début"));
        }

        // Vérifier mode de jouissance
        if (dto.getModeJouissance() == null || !GlobalEnums.ModeJouissanceConge.isValidModeJouissanceConge(dto.getModeJouissance())) {
            errors.add(new ErrorResponse("modeDeJouissance", "Le mode de jouissance est invalide ou manquant. Valeurs acceptées : NUMERAIRE, REEL, DIFFERE"));
        }

        // Vérifier condition d'acceptation
        if (dto.getConditionAcceptation() == null || !GlobalEnums.ConditionAcceptationConge.isValidConditionAcceptationConge(dto.getConditionAcceptation())) {
            errors.add(new ErrorResponse("conditionAcceptationConge", "La condition est invalide ou manquante. Valeurs acceptées : A_DEDUIRE_DES_CONGES, A_DEDUIRE_DU_SALAIRE_DE_PRESENCE, SANS_CONDITION"));
        }

        // Vérifier existence d'une absence déjà enregistrée
//        if (dto.getContratEmployeId() != null && dateDebut != null && dateFin != null) {
//            boolean existe = absenceRepository.existsAbsenceOverlap(
//                    dto.getContratEmployeId(),
//                    dateDebut,
//                    dateFin
//            );
//
//            if (existe) {
//                errors.add(new ErrorResponse("absenceExistante", "Une absence existe déjà pour cette période"));
//            }
//        }

        return errors;
    }


    public List<ErrorResponse> getInvalidFieldMessagesForUpdate(Long id, AbsenceDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérifier existence de l'absence
        Absence absence = absenceRepository.findById(id).orElse(null);
        if (absence == null) {
            errors.add(new ErrorResponse("id", "Absence introuvable"));
            return errors;
        }

        LocalDate dateDebut = dto.getDateDebut() != null ? dto.getDateDebut() : absence.getDateDebut();
        LocalDate dateFin = dto.getDateFin() != null ? dto.getDateFin() : absence.getDateFin();

        // Vérifier cohérence des dates
        if (dateDebut != null && dateFin != null && dateDebut.isAfter(dateFin)) {
            errors.add(new ErrorResponse("dateFin", "La date de fin doit être postérieure à la date de début"));
        }

        if (dto.getTypeAbsenceId() != null && !typeAbsenceRepository.existsById(dto.getTypeAbsenceId())) {
            errors.add(new ErrorResponse("typeAbsenceId", "Type d’absence introuvable"));
        }

        if (dto.getModeJouissance() != null && !GlobalEnums.ModeJouissanceConge.isValidModeJouissanceConge(dto.getModeJouissance())) {
            errors.add(new ErrorResponse("modeJouissance", "Le mode de jouissance est invalide. Valeurs acceptées : NUMERAIRE, REEL, DIFFERE"));
        }

        if (dto.getConditionAcceptation() == null || !GlobalEnums.ConditionAcceptationConge.isValidConditionAcceptationConge(dto.getConditionAcceptation())) {
            errors.add(new ErrorResponse("conditionAcceptation", "La condition est invalide. Valeurs acceptées : A_DEDUIRE_DES_CONGES, A_DEDUIRE_DU_SALAIRE_DE_PRESENCE, SANS_CONDITION"));
        }


        return errors;
    }

}
