package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeResponseDTO(

        Long id,
        String matricule,
        String titre,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String lieuNaissance,
        String sexe,
        String situationMatrimoniale,
        String numeroIfu,
        String telephone,
        String email,
        String nomPere,
        String nomMere,
        String boitePostale,
        String maison,
        String numeroCarre,
        String quartier,
        String nationalite,
        String numeroCnss,
        String profession,
        Boolean isEmployeInterne,

        // Infos société
        Long companyId,
        String companyName,

        // Champs calculés
        Integer age,
        LocalDate prochainAnniversaire,
        Long joursRestants,

        // Audit
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}