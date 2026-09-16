package com.tpc.tpcgestpaie.localapp.dto.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanMensuelChargeSocialeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilanMensuelChargeSocialeExportDTO {
    private String mois;
    private String entreprise;
    private String employe;
    private String numeroCnss;
    private BigDecimal salaireBrut;
    private BigDecimal cnssEmploye;
    private BigDecimal cnssEmployeur;
    private BigDecimal totalChargeSociale;

    // Constructeur depuis BilanMensuelChargeSocialeDTO
    public BilanMensuelChargeSocialeExportDTO(BilanMensuelChargeSocialeDTO dto, String numeroCnss) {
        this.mois = dto.getMoisBulletinPaie();
        this.entreprise = dto.getEntreprise();
        this.employe = dto.getEmploye();
        this.numeroCnss = numeroCnss;
        this.salaireBrut = dto.getSalaireBrutBulletinPaie();
        this.cnssEmploye = dto.getMontantCnssBulletinPaie();
        this.cnssEmployeur = dto.getMontantCnssEmployeurBulletinPaie();
        this.totalChargeSociale = dto.getTotalChargeSociale();
    }
}