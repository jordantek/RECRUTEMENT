package com.tpc.tpcgestpaie.localapp.dto;

import java.math.BigDecimal;

public class RubriqueMontantDTO {
    private Long rubriqueId;
    private String libelle;
    private Double montantBase;
    private Double montantTotal;

    public RubriqueMontantDTO(Long rubriqueId, String libelle, Double montantAjouter, Double montantTotal) {
        this.rubriqueId = rubriqueId;
        this.libelle = libelle;
        this.montantBase = montantAjouter;
        this.montantTotal = montantTotal;
    }

    public static RubriqueMontantDTO fromTuple(Object[] tuple) {
        return new RubriqueMontantDTO(
                (Long) tuple[0],
                (String) tuple[1],
                ((BigDecimal) tuple[2]).doubleValue(), // montantAjouter
                ((BigDecimal) tuple[3]).doubleValue()  // montantTotal
        );
    }

    public Long getRubriqueId() { return rubriqueId; }
    public void setRubriqueId(Long rubriqueId) { this.rubriqueId = rubriqueId; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Double getMontantBase() { return montantBase; }
    public void setMontantBase(Double montantAjouter) { this.montantBase = montantAjouter; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
}
