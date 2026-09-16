package com.tpc.tpcgestpaie.localapp.dto.event;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.HrEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HrEventRequestDTO(
        Long id,
        Long employeeId,                       // ID de l'employé
        EmployeDTO employee,                  // Infos facultatives de l'employé (peut être null)
        Long contratEmployeId,                 // ID du contrat employé
        ContractEmployeDTO contrat,             // Infos facultatives du contrat (peut être null)
        HrEventType hrEventType,
        Long companyId,                        // ID de l'entreprise
        CompanyDTO company,                    // Infos facultatives de l'entreprise (peut être null)
        String type,                           // Type d'événement
        LocalDate eventDate,                   // Date de l'événement
        boolean recurring,                     // Récurrent ou unique
        LocalDate endDate,                     // Date de fin (pour événements récurrents)
        LocalDateTime createdAt,
        String title,
        String description,
        Boolean actionRequired
) {}