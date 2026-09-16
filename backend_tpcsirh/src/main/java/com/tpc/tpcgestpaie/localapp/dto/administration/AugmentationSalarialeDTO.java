package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public class AugmentationSalarialeDTO {


    private Long id;
    private Long employeId;
    private EmployeDTO  employe;
    private Long contratEmployeId;
    private Long companyId;
    private LocalDate dateEffet;
    private String motifAugmentation;
    private String preuve; // chemin ou nom fichier
    private List<AugmentationRubriqueDTO> rubriques;
    private User added_by;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ENTITY -> DTO
    public static AugmentationSalarialeDTO fromEntity(AugmentationSalariale entity) {
        if (entity == null) return null;

        AugmentationSalarialeDTO dto = new AugmentationSalarialeDTO();
        dto.setId(entity.getId());
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setDateEffet(entity.getDateEffet());
        dto.setMotifAugmentation(entity.getMotifAugmentation());
        dto.setPreuve(entity.getPreuve());
        dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));

        if (entity.getRubriques() != null) {
            dto.setRubriques(entity.getRubriques().stream()
                    .map(AugmentationSalarialeDTO::fromAugmentationRubriqueEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private static AugmentationSalarialeDTO.AugmentationRubriqueDTO fromAugmentationRubriqueEntity(AugmentationRubrique entity) {
        if (entity == null) return null;

        AugmentationSalarialeDTO.AugmentationRubriqueDTO dto = new AugmentationSalarialeDTO.AugmentationRubriqueDTO();
        dto.setId(entity.getId());
        dto.setRubriqueId(entity.getRubrique() != null ? entity.getRubrique().getId() : null);
        dto.setLibelle(entity.getLibelle());
        dto.setAncienMontant(entity.getAncienMontant());
        dto.setNouveauMontant(entity.getNouveauMontant());
        dto.setMontantAugmentation(entity.getMontantAugmentation());
        dto.setEstNouvelleRubrique(entity.isEstNouvelleRubrique());
        return dto;
    }

    // DTO -> ENTITY
    public static AugmentationSalariale toEntity(AugmentationSalarialeDTO dto) {
        if (dto == null) return null;

        AugmentationSalariale entity = new AugmentationSalariale();
        entity.setId(dto.getId());

        // Remarque : ici on ne remplit pas les entités Employe, ContratEmploye, Company
        // uniquement leurs IDs sont disponibles dans le DTO, donc à gérer côté service

        entity.setDateEffet(dto.getDateEffet());
        entity.setMotifAugmentation(dto.getMotifAugmentation());
        entity.setPreuve(dto.getPreuve());

        if (dto.getRubriques() != null) {
            entity.setRubriques(dto.getRubriques().stream()
                    .map(AugmentationSalarialeDTO::toAugmentationRubriqueEntity)
                    .collect(Collectors.toList()));
            // Attention : il faut lier l'augmentation à chaque rubrique
            entity.getRubriques().forEach(rub -> rub.setAugmentation(entity));
        }


        return entity;
    }

    public static AugmentationRubrique toAugmentationRubriqueEntity(AugmentationSalarialeDTO.AugmentationRubriqueDTO dto) {
        if (dto == null) return null;

        AugmentationRubrique entity = new AugmentationRubrique();
        entity.setId(dto.getId());
        // Remplissage de rubrique seulement avec id (attention à la récupération en base en service)
        if (dto.getRubriqueId() != null) {
            Rubrique rubrique = new Rubrique();
            rubrique.setId(dto.getRubriqueId());
            entity.setRubrique(rubrique);
        }
        entity.setLibelle(dto.getLibelle());
        entity.setAncienMontant(dto.getAncienMontant());
        entity.setNouveauMontant(dto.getNouveauMontant());
        entity.setMontantAugmentation(dto.getMontantAugmentation());
        entity.setEstNouvelleRubrique(dto.isEstNouvelleRubrique());

        return entity;
    }


    // Getters / Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public EmployeDTO  getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO  employe) {
        this.employe = employe;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public String getMotifAugmentation() {
        return motifAugmentation;
    }

    public void setMotifAugmentation(String motifAugmentation) {
        this.motifAugmentation = motifAugmentation;
    }

    public String getPreuve() {
        return preuve;
    }

    public void setPreuve(String preuve) {
        this.preuve = preuve;
    }

    public List<AugmentationRubriqueDTO> getRubriques() {
        return rubriques;
    }

    public void setRubriques(List<AugmentationRubriqueDTO> rubriques) {
        this.rubriques = rubriques;
    }

    public User getAdded_by() {
        return added_by;
    }

    public void setAdded_by(User added_by) {
        this.added_by = added_by;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private static EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

    // DTO interne pour les rubriques
    public static class AugmentationRubriqueDTO {
        private Long id;
        private Long rubriqueId;
        private String libelle;
        private BigDecimal ancienMontant;
        private BigDecimal nouveauMontant;
        private BigDecimal montantAugmentation;
        private boolean estNouvelleRubrique;

        // Getters / Setters

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getRubriqueId() {
            return rubriqueId;
        }

        public void setRubriqueId(Long rubriqueId) {
            this.rubriqueId = rubriqueId;
        }

        public String getLibelle() {
            return libelle;
        }

        public void setLibelle(String libelle) {
            this.libelle = libelle;
        }

        public BigDecimal getAncienMontant() {
            return ancienMontant;
        }

        public void setAncienMontant(BigDecimal ancienMontant) {
            this.ancienMontant = ancienMontant;
        }

        public BigDecimal getNouveauMontant() {
            return nouveauMontant;
        }

        public void setNouveauMontant(BigDecimal nouveauMontant) {
            this.nouveauMontant = nouveauMontant;
        }

        public BigDecimal getMontantAugmentation() {
            return montantAugmentation;
        }

        public void setMontantAugmentation(BigDecimal montantAugmentation) {
            this.montantAugmentation = montantAugmentation;
        }

        public boolean isEstNouvelleRubrique() {
            return estNouvelleRubrique;
        }

        public void setEstNouvelleRubrique(boolean estNouvelleRubrique) {
            this.estNouvelleRubrique = estNouvelleRubrique;
        }
    }
}
