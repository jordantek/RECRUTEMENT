package com.tpc.tpcgestpaie.localapp.dto.jourtravail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoursTravaillesDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private boolean lundi;
    private boolean mardi;
    private boolean mercredi;
    private boolean jeudi;
    private boolean vendredi;
    private boolean samedi;
    private boolean dimanche;
    private Integer joursSemaine;
    private String description;

    // Méthodes pratiques
    public boolean estJourTravaille(String jour) {
        return switch (jour.toLowerCase()) {
            case "lundi" -> lundi;
            case "mardi" -> mardi;
            case "mercredi" -> mercredi;
            case "jeudi" -> jeudi;
            case "vendredi" -> vendredi;
            case "samedi" -> samedi;
            case "dimanche" -> dimanche;
            default -> false;
        };
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (lundi) sb.append("Lun ");
        if (mardi) sb.append("Mar ");
        if (mercredi) sb.append("Mer ");
        if (jeudi) sb.append("Jeu ");
        if (vendredi) sb.append("Ven ");
        if (samedi) sb.append("Sam ");
        if (dimanche) sb.append("Dim ");
        return sb.toString().trim();
    }
}