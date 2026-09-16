package com.tpc.tpcgestpaie.localapp.dto.event;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;

import java.time.LocalDate;

public record TrainingStartEvent(Employe employe, Company company, LocalDate startDate) {}