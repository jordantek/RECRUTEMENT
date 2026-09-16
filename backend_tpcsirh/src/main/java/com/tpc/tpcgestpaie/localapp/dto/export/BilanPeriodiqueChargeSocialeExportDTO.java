package com.tpc.tpcgestpaie.localapp.dto.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanPeriodiqueChargeSocialeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilanPeriodiqueChargeSocialeExportDTO {
    private String mois;
    private String entreprise;
    private BigDecimal salaireBrut;
    private BigDecimal cnssEmploye;
    private BigDecimal cnssEmployeur;
    private BigDecimal totalChargeSociale;

    // Constructeur depuis BilanPeriodiqueChargeSocialeDTO
    public BilanPeriodiqueChargeSocialeExportDTO(BilanPeriodiqueChargeSocialeDTO dto, String entrepriseNom) {
        this.mois = dto.getDebut(); // Utiliser début comme mois
        this.entreprise = entrepriseNom;
        this.salaireBrut = dto.getSalaireBrut();
        this.cnssEmploye = dto.getCnssEmploye();
        this.cnssEmployeur = dto.getCnssEmployeur();
        this.totalChargeSociale = dto.getTotalChargeSociale();
    }
}