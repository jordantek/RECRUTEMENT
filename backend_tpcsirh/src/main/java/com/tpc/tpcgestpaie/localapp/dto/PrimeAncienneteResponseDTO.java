package com.tpc.tpcgestpaie.localapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrimeAncienneteResponseDTO {
    private boolean enabled;
    private Double primeAnciennete;
    private Double salaireBase;
    private Integer ancienneteAnnees;
    private String message;

    // Constructeur pour succès
    public PrimeAncienneteResponseDTO(boolean enabled, Double primeAnciennete, Double salaireBase, Integer ancienneteAnnees) {
        this.enabled = enabled;
        this.primeAnciennete = primeAnciennete;
        this.salaireBase = salaireBase;
        this.ancienneteAnnees = ancienneteAnnees;
        this.message = null;
    }

    // Constructeur pour erreur
    public PrimeAncienneteResponseDTO(String message) {
        this.message = message;
        this.enabled = false;
        this.primeAnciennete = 0.0;
        this.salaireBase = 0.0;
        this.ancienneteAnnees = 0;
    }
}