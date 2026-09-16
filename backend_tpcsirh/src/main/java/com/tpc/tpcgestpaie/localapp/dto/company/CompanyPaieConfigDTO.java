package com.tpc.tpcgestpaie.localapp.dto.company;

import com.tpc.tpcgestpaie.localapp.model.Company;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompanyPaieConfigDTO {
    private Long id;
    private Double vps;
    private LocalDate vpsEffectDate;
    private String signatoryName;
    private BigDecimal ca;
    private String modeJouissanceConge;
    private Double nbrJourTravail;
    private Double nbrJourConge;
    private Double heuresParJour;
    private Double heuresParSemaine;

    public CompanyPaieConfigDTO(Company company) {
        this.id = company.getId();
        this.vps = company.getVps();
        this.vpsEffectDate = company.getVpsEffectDate();
        this.signatoryName = company.getSignatoryName();
        this.ca = company.getCa();
        this.modeJouissanceConge = company.getModeJouissanceConge();
        this.nbrJourTravail = company.getNbrJourTravail();
        this.nbrJourConge = company.getNbrJourConge();
        this.heuresParJour = company.getHeuresParJour();
        this.heuresParSemaine = company.getHeuresParSemaine();
    }
}