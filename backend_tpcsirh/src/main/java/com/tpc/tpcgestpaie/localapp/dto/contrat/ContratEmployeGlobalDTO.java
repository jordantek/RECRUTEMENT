package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDiplomeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContratEmployeGlobalDTO {

    private ContractEmployeDTO contratEmploye;
    private List<EmployeDiplomeDTO> diplomes;
    private List<ContratEmployeRubriqueDTO> rubriques;

    private static final ObjectMapper mapper = new ObjectMapper();

    // --- Méthodes utilitaires JSON ---
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // support LocalDate, LocalDateTime
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // format lisible
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur de sérialisation du ContratEmployeGlobalDTO", e);
        }
    }

    // Convertir un JSON en DTO
    public static ContratEmployeGlobalDTO fromJson(String json) {
        try {
            return mapper.readValue(json, ContratEmployeGlobalDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur de désérialisation vers ContratEmployeGlobalDTO", e);
        }
    }

    // --- Getters & Setters ---
    public ContractEmployeDTO getContratEmploye() {
        return contratEmploye;
    }

    public void setContratEmploye(ContractEmployeDTO contratEmploye) {
        this.contratEmploye = contratEmploye;
    }

    public List<EmployeDiplomeDTO> getDiplomes() {
        return diplomes;
    }

    public void setDiplomes(List<EmployeDiplomeDTO> diplomes) {
        this.diplomes = diplomes;
    }

    public List<ContratEmployeRubriqueDTO> getRubriques() {
        return rubriques;
    }

    public void setRubriques(List<ContratEmployeRubriqueDTO> rubriques) {
        this.rubriques = rubriques;
    }
}
