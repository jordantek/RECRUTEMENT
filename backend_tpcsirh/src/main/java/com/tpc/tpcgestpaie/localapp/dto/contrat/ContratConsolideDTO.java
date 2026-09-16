package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;

import java.util.List;

public class ContratConsolideDTO {
    private ContratEmploye contratInitial;
    private List<ModificationContrat> modifications;
    private ContratEmploye contratFinal;

    // Getters et Setters


    public ContratEmploye getContratInitial() {
        return contratInitial;
    }

    public void setContratInitial(ContratEmploye contratInitial) {
        this.contratInitial = contratInitial;
    }

    public List<ModificationContrat> getModifications() {
        return modifications;
    }

    public void setModifications(List<ModificationContrat> modifications) {
        this.modifications = modifications;
    }

    public ContratEmploye getContratFinal() {
        return contratFinal;
    }

    public void setContratFinal(ContratEmploye contratFinal) {
        this.contratFinal = contratFinal;
    }
}

