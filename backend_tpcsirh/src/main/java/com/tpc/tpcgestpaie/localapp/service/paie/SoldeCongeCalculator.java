package com.tpc.tpcgestpaie.localapp.service.paie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.SoldeCongeResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.CreditConge;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.service.administration.CreditCongeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoldeCongeCalculator {

    private final ContratEmployeService contratEmployeService;
    private final CreditCongeService creditCongeService;
    private final AbsenceService absenceService;

    @Autowired
    public SoldeCongeCalculator(
            ContratEmployeService contratEmployeService,
            CreditCongeService creditCongeService,
            AbsenceService absenceService
    ) {
        this.contratEmployeService = contratEmployeService;
        this.creditCongeService = creditCongeService;
        this.absenceService = absenceService;
    }

    public SoldeCongeResponseDTO calculerSoldeConge(Long idContratEmploye) {
        Optional<ContractEmployeDTO> contrat = contratEmployeService.getById(idContratEmploye);
        if (contrat.isEmpty()) {
            return null;
        }

        ContractEmployeDTO contratEmploye = contrat.get();
        CreditConge dernierCredit = creditCongeService.getCreditCongeActifByIdEmploye(contratEmploye.getEmployeId());
        SoldeCongeResponseDTO dto = new SoldeCongeResponseDTO();

        LocalDate dateReference = (dernierCredit != null)
                ? dernierCredit.getDateReference()
                : LocalDate.now();
        String formattedDate = dateReference.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dto.setDateReferenceCreditConge(formattedDate);
        int creditConge = (dernierCredit != null)
                ? absenceService.calculateCreditConge(dateReference)
                : 0;
        dto.setCreditConge(creditConge);
        int totalPris = absenceService.getDureeTotalAbsenceByIdContratEmploye(idContratEmploye);
        dto.setNombreTotalJourPris(totalPris);
        dto.setSoldeConge(creditConge - totalPris);

        return dto;
    }
}
