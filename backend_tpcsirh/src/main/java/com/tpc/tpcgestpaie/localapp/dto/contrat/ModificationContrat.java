package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.tpc.tpcgestpaie.localapp.model.StatutContrat;

import java.time.LocalDate;
import java.util.Map;

public class ModificationContrat {
    private StatutContrat statut;
    private String typeModification;
    private LocalDate dateEffet;
    private String motif;
    private String preuveFilename;
    private Map<String, Object> differences;

    // Getters et Setters


    public StatutContrat getStatut() {
        return statut;
    }

    public void setStatut(StatutContrat statut) {
        this.statut = statut;
    }

    public String getTypeModification() {
        return typeModification;
    }

    public void setTypeModification(String typeModification) {
        this.typeModification = typeModification;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getPreuveFilename() {
        return preuveFilename;
    }

    public void setPreuveFilename(String preuveFilename) {
        this.preuveFilename = preuveFilename;
    }

    public Map<String, Object> getDifferences() {
        return differences;
    }

    public void setDifferences(Map<String, Object> differences) {
        this.differences = differences;
    }
}
