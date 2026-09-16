package com.tpc.tpcgestpaie.localapp.dto.employe;


import com.tpc.tpcgestpaie.localapp.dto.EnfantEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.PersonneAPrevenirDTO;

import java.util.List;

public class EmployeFamilleDTO{
    private EmployeDTO employe;
    private List<EnfantEmployeDTO> enfants;
    private List<PersonneAPrevenirDTO> personnesAPrevenir;

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public List<EnfantEmployeDTO> getEnfants() {
        return enfants;
    }

    public void setEnfants(List<EnfantEmployeDTO> enfants) {
        this.enfants = enfants;
    }

    public List<PersonneAPrevenirDTO> getPersonnesAPrevenir() {
        return personnesAPrevenir;
    }

    public void setPersonnesAPrevenir(List<PersonneAPrevenirDTO> personnesAPrevenir) {
        this.personnesAPrevenir = personnesAPrevenir;
    }
}
