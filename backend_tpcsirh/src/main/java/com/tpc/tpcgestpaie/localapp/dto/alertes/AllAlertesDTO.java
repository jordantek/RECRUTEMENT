package com.tpc.tpcgestpaie.localapp.dto.alertes;

import java.util.List;

public class AllAlertesDTO {

    private List<?> anniversaires;
    private List<?> finContrats;
    private List<?> finEssais;
    private List<?> debutAbsences;
    private  List<?> finAbsences;

    public AllAlertesDTO(List<?> anniversaires, List<?> finContrat, List<?> finEssais,List<?> debutAbsences, List<?> finAbsences) {
        this.anniversaires = anniversaires;
        this.finContrats = finContrat;
        this.finEssais = finEssais;
        this.debutAbsences = debutAbsences;
        this.finAbsences = finAbsences;
    }

    public List<?> getAnniversaires() {
        return anniversaires;
    }

    public void setAnniversaires(List<?> anniversaires) {
        this.anniversaires = anniversaires;
    }

    public List<?> getFinContrats() {
        return finContrats;
    }

    public void setFinContrats(List<?> finContrat) {
        this.finContrats = finContrat;
    }

    public List<?> getFinEssais() {
        return finEssais;
    }

    public void setFinEssais(List<?> finEssais) {
        this.finEssais = finEssais;
    }


    public List<?> getDebutAbsences() {
        return debutAbsences;
    }

    public void setDebutAbsences(List<?> debutAbsence) {
        this.debutAbsences = debutAbsence;
    }

    public List<?> getFinAbsences(){
        return  finAbsences;
    }

    public  void setFinAbsences(List<?> finAbsences) {
        this.finAbsences = finAbsences;
    }
}
