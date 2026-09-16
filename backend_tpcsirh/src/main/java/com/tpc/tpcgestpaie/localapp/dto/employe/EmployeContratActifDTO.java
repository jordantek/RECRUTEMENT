package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeContratActifDTO {

    private String nom;
    private String prenom;
    private Long employeId;
    private Long contratEmployeId;
    private String matricule;
    private Long companyId;
    private LocalDate date_debut;
    private LocalDate date_fin;

    private List<ContratEmployeRubriqueDTO> rubriques;

    public EmployeContratActifDTO(String nom, String prenom, Long employeId, Long contratEmployeId, String matricule, Long companyId) {
        this.nom = nom;
        this.prenom = prenom;
        this.employeId = employeId;
        this.contratEmployeId = contratEmployeId;
        this.matricule = matricule;
        this.companyId = companyId;
    }

    public EmployeContratActifDTO() {}

    public static EmployeContratActifDTO fromEntity(ContratEmploye c) {
        EmployeContratActifDTO dto = new EmployeContratActifDTO();
        dto.setNom(c.getEmploye().getNom());
        dto.setPrenom(c.getEmploye().getPrenom());
        dto.setEmployeId(c.getEmploye().getId());
        dto.setContratEmployeId(c.getId());
        dto.setMatricule(c.getEmploye().getMatricule());
        dto.setCompanyId(c.getCompany().getId());
        dto.setDate_debut(c.getDate_debut());
        dto.setDate_fin(c.getDate_fin());

        // 🔥 Ajouter la conversion des rubriques
        if (c.getRubriques() != null) {
            List<ContratEmployeRubriqueDTO> rubriqueDTOs = c.getRubriques()
                    .stream()
                    .map(ContratEmployeRubriqueDTO::fromEntity)
                    .collect(Collectors.toList());
            dto.setRubriques(rubriqueDTOs);
        }

        return dto;
    }

    // Getters et setters

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public Long getContratEmployeId() { return contratEmployeId; }
    public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public LocalDate getDate_debut() { return date_debut; }
    public void setDate_debut(LocalDate date_debut) { this.date_debut = date_debut; }

    public LocalDate getDate_fin() { return date_fin; }
    public void setDate_fin(LocalDate date_fin) { this.date_fin = date_fin; }

    public List<ContratEmployeRubriqueDTO> getRubriques() { return rubriques; }
    public void setRubriques(List<ContratEmployeRubriqueDTO> rubriques) { this.rubriques = rubriques; }
}
