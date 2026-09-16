package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;

import java.util.List;

public class EmployeGlobalDTO {
    private Employe employe;
    private List<EnfantEmploye> enfants;
    private List<PersonneAPrevenir> personnesAPrevenir;

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public List<EnfantEmploye> getEnfants() {
        return enfants;
    }

    public void setEnfants(List<EnfantEmploye> enfants) {
        this.enfants = enfants;
    }

    public List<PersonneAPrevenir> getPersonnesAPrevenir() {
        return personnesAPrevenir;
    }

    public void setPersonnesAPrevenir(List<PersonneAPrevenir> personnesAPrevenir) {
        this.personnesAPrevenir = personnesAPrevenir;
    }
}