package com.tpc.tpcgestpaie.localapp.dto.event;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;

import java.time.LocalDate;

public record EmployeChangedEvent(
        Employe employe,
        ContratEmploye contrat,
        Company company
) {}



