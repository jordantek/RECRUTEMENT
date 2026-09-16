package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.model.Employe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeDTO {

    private Long id;
    private String matricule;
    private String titre;
    private String nom;
    private String prenom;
    private LocalDate date_naissance;
    private String lieu_naissance;
    private String sexe;
    private String situationMatrimoniale;
    private String numero_ifu;
    private String telephone;
    private String email;
    private String nom_pere;
    private String nom_mere;
    private String boite_postale;
    private String maison;
    private String numeroCarre;
    private String quartier;
    private String nationalite;
    private String numero_cnss;
    private String profession;
    private boolean is_employe_interne;

    private Long added_by_id;
    private Long updated_by_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    // 💡 Champs pour éviter LazyLoading sur les formations

    private List<Long> formationIds;

    // ===============================
    // Conversion entity -> DTO
    // ===============================
    public static EmployeDTO fromEntity(Employe entity) {
        if (entity == null) {
            return null;
        }

        EmployeDTO dto = new EmployeDTO();
        dto.setId(entity.getId());
        dto.setMatricule(entity.getMatricule());
        dto.setTitre(entity.getTitre());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setDate_naissance(entity.getDate_naissance());
        dto.setLieu_naissance(entity.getLieu_naissance());
        dto.setSexe(entity.getSexe());
        dto.setSituationMatrimoniale(entity.getSituationMatrimoniale());
        dto.setNumero_ifu(entity.getNumero_ifu());
        dto.setTelephone(entity.getTelephone());
        dto.setEmail(entity.getEmail());
        dto.setNom_pere(entity.getNom_pere());
        dto.setNom_mere(entity.getNom_mere());
        dto.setBoite_postale(entity.getBoite_postale());
        dto.setMaison(entity.getMaison());
        dto.setNumeroCarre(entity.getNumeroCarre());
        dto.setQuartier(entity.getQuartier());
        dto.setNationalite(entity.getNationalite());
        dto.setNumero_cnss(entity.getNumero_cnss());
        dto.setProfession(entity.getProfession());
        dto.setIs_employe_interne(entity.isIs_employe_interne());

        dto.setAdded_by_id(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setUpdated_by_id(entity.getUpdated_by() != null ? entity.getUpdated_by().getId() : null);
        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());
        dto.setDeleted_at(entity.getDeleted_at());

        if (entity.getFormations() != null) {
            dto.setFormationIds(
                    entity.getFormations().stream()
                            .map(f -> f.getId())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // ===============================
    // Conversion DTO -> entity (basique)
    // ===============================
    public static Employe toEntity(EmployeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employe entity = new Employe();
        entity.setId(dto.getId());
        entity.setMatricule(dto.getMatricule());
        entity.setTitre(dto.getTitre());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setDate_naissance(dto.getDate_naissance());
        entity.setLieu_naissance(dto.getLieu_naissance());
        entity.setSexe(dto.getSexe());
        entity.setSituationMatrimoniale(dto.getSituationMatrimoniale());
        entity.setNumero_ifu(dto.getNumero_ifu());
        entity.setTelephone(dto.getTelephone());
        entity.setEmail(dto.getEmail());
        entity.setNom_pere(dto.getNom_pere());
        entity.setNom_mere(dto.getNom_mere());
        entity.setBoite_postale(dto.getBoite_postale());
        entity.setMaison(dto.getMaison());
        entity.setNumeroCarre(dto.getNumeroCarre());
        entity.setQuartier(dto.getQuartier());
        entity.setNationalite(dto.getNationalite());
        entity.setNumero_cnss(dto.getNumero_cnss());
        entity.setProfession(dto.getProfession());
        entity.setIs_employe_interne(dto.isIs_employe_interne());

        entity.setCreated_at(dto.getCreated_at());
        entity.setUpdated_at(dto.getUpdated_at());
        entity.setDeleted_at(dto.getDeleted_at());

        // ⚠ Les formations devront être ajoutées dans le service, pas ici (car il faut charger les entités)

        return entity;
    }

    // ===============================
    // Getters / Setters
    // ===============================

    // (Les tiens + formationIds)
    public List<Long> getFormationIds() {
        return formationIds;
    }

    public void setFormationIds(List<Long> formationIds) {
        this.formationIds = formationIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDate_naissance() { return date_naissance; }
    public void setDate_naissance(LocalDate date_naissance) { this.date_naissance = date_naissance; }

    public String getLieu_naissance() { return lieu_naissance; }
    public void setLieu_naissance(String lieu_naissance) { this.lieu_naissance = lieu_naissance; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getSituationMatrimoniale() { return situationMatrimoniale; }
    public void setSituationMatrimoniale(String situationMatrimoniale) { this.situationMatrimoniale = situationMatrimoniale; }

    public String getNumero_ifu() { return numero_ifu; }
    public void setNumero_ifu(String numero_ifu) { this.numero_ifu = numero_ifu; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom_pere() { return nom_pere; }
    public void setNom_pere(String nom_pere) { this.nom_pere = nom_pere; }

    public String getNom_mere() { return nom_mere; }
    public void setNom_mere(String nom_mere) { this.nom_mere = nom_mere; }

    public String getBoite_postale() { return boite_postale; }
    public void setBoite_postale(String boite_postale) { this.boite_postale = boite_postale; }

    public String getMaison() { return maison; }
    public void setMaison(String maison) { this.maison = maison; }

    public String getNumeroCarre() { return numeroCarre; }
    public void setNumeroCarre(String numeroCarre) { this.numeroCarre = numeroCarre; }

    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }

    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }

    public String getNumero_cnss() { return numero_cnss; }
    public void setNumero_cnss(String numero_cnss) { this.numero_cnss = numero_cnss; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public boolean isIs_employe_interne() { return is_employe_interne; }
    public void setIs_employe_interne(boolean is_employe_interne) { this.is_employe_interne = is_employe_interne; }

    public Long getAdded_by_id() { return added_by_id; }
    public void setAdded_by_id(Long added_by_id) { this.added_by_id = added_by_id; }

    public Long getUpdated_by_id() { return updated_by_id; }
    public void setUpdated_by_id(Long updated_by_id) { this.updated_by_id = updated_by_id; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getUpdated_at() { return updated_at; }
    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }

    public LocalDateTime getDeleted_at() { return deleted_at; }
    public void setDeleted_at(LocalDateTime deleted_at) { this.deleted_at = deleted_at; }

}
