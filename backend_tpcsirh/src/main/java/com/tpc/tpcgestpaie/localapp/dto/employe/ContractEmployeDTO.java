package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.DepartementDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractEmployeDTO {

    private Long id;

    // ID + objet complet pour Employe
    private Long employeId;
    private Employe employe;

    // ID + objet complet pour Company
    private Long companyId;

    // Même principe pour les autres relations
    private Long departementId;
    private Departement departement;
    private DepartementDTO departementDTO;

    private Long posteId;
    private Poste poste;
    private Long categorieEmployeId;
    private CategorieEmploye categorieEmploye;
    private Long natureContratId;
    private NatureContrat natureContrat;

    private String diplome_requis;
    private String missions;
    private String lieu_execution;
    private String numeroContrat;

    private Long modeDePaiementId;
    private ModeDePaiement modeDePaiement;
    private Long banqueId;
    private Banque banque;

    private Long addedById;
    @JsonIgnore
    private User addedBy;

    // Les autres champs simples restent inchangés

    private String mouvementContrat;
    private String typeContrat;
    private String numeroCompte;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String duree;

    private LocalDate debutEssai;
    private LocalDate finEssai;
    private String dureeEssai;

    private LocalDate dateEmbauche;

    private String dureeContrat;

    private double aibContratEmploye;
    private double cautionContratEmploye;
    private double transfertContratEmploye;

    private String statusContrat;
    private boolean arretContrat;
    private LocalDate dateArretContrat;
    private String motifArretContrat;

    private Double salaire_brut;
    private Double salaire_base;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // ============================================
    // NOUVEAUX ATTRIBUTS POUR AVENANTS
    // ============================================

    // ⏰ Horaire de travail
    private String horaireTravail;

    // 🏢 Nature juridique de l'employeur (SA, SARL, SAS, etc.)
    private String natureJuridiqueEmployeur;

    // 📝 Situations pour avenants
    private String ancienneSituation;
    private String nouvelleSituation;

    // 💬 Commentaire libre
    private String commentaire;

    // ============================================
    // MAPPERS MIS À JOUR
    // ============================================

    public static ContractEmployeDTO fromEntity(ContratEmploye entity) {

        if (entity == null) {
            return null;
        }

        ContractEmployeDTO dto = new ContractEmployeDTO();
        dto.setId(entity.getId());

        if (entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmploye(entity.getEmploye());
        }

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
        }

        if (entity.getDepartement() != null) {
            dto.setDepartementId(entity.getDepartement().getId());
            dto.setDepartement(entity.getDepartement());
        }

        if (entity.getPoste() != null) {
            dto.setPosteId(entity.getPoste().getId());
            dto.setPoste(entity.getPoste());
        }

        if (entity.getCategorieEmploye() != null) {
            dto.setCategorieEmployeId(entity.getCategorieEmploye().getId());
            dto.setCategorieEmploye(entity.getCategorieEmploye());
        }

        if (entity.getNatureContrat() != null) {
            dto.setNatureContratId(entity.getNatureContrat().getId());
            dto.setNatureContrat(entity.getNatureContrat());
        }

        if (entity.getModeDePaiement() != null) {
            dto.setModeDePaiementId(entity.getModeDePaiement().getId());
            dto.setModeDePaiement(entity.getModeDePaiement());
        }

        if (entity.getBanque() != null) {
            dto.setBanqueId(entity.getBanque().getId());
            dto.setBanque(entity.getBanque());
        }

        if (entity.getAdded_by() != null) {
            dto.setAddedById(entity.getAdded_by().getId());
            dto.setAddedBy(entity.getAdded_by());
        }

        dto.setHoraireTravail(entity.getHoraireTravail());
        dto.setNumeroContrat(entity.getNumeroContrat());
        dto.setLieu_execution(entity.getLieu_execution());
        dto.setDiplome_requis(entity.getDiplome_requis());
        dto.setMissions(entity.getMissions());
        dto.setMouvementContrat(entity.getMouvement_contrat());
        dto.setTypeContrat(entity.getType_contrat());
        dto.setNumeroCompte(entity.getNumero_compte());
        dto.setDateDebut(entity.getDate_debut());
        dto.setDateFin(entity.getDate_fin());
        dto.setDuree(entity.getDuree());
        dto.setDebutEssai(entity.getDebut_essai());
        dto.setFinEssai(entity.getFin_essai());

        dto.setDateEmbauche(entity.getDateEmbauche());

        dto.setDureeEssai(entity.getDuree_essai());
        dto.setDureeContrat(entity.getDuree_contrat());
        dto.setAibContratEmploye(entity.getAib_contrat_employe());
        dto.setCautionContratEmploye(entity.getCaution_contrat_employe());
        dto.setTransfertContratEmploye(entity.getTransfert_contrat_employe());
        dto.setStatusContrat(entity.getStatus_contrat());
        dto.setArretContrat(entity.isArretContrat());
        dto.setDateArretContrat(entity.getDate_arret_contrat());
        dto.setMotifArretContrat(entity.getMotif_arret_contrat());
        dto.setSalaire_brut(entity.getSalaire_brut());
        dto.setSalaire_base(entity.getSalaire_base());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // ============================================
        // MAPPING NOUVEAUX ATTRIBUTS
        // ============================================
        dto.setNatureJuridiqueEmployeur(entity.getNatureJuridiqueEmployeur());
        dto.setAncienneSituation(entity.getAncienneSituation());
        dto.setNouvelleSituation(entity.getNouvelleSituation());
        dto.setCommentaire(entity.getCommentaire());

        return dto;
    }

    public ContratEmploye toEntity() {
        ContratEmploye entity = new ContratEmploye();
        entity.setId(this.id);

        // Si objet complet présent, on l'utilise, sinon on crée avec juste l'ID
        if (this.employe != null) {
            entity.setEmploye(this.employe);
        } else if (this.employeId != null) {
            Employe e = new Employe();
            e.setId(this.employeId);
            entity.setEmploye(e);
        }

        if (this.companyId != null) {
            Company c = new Company();
            c.setId(this.companyId);
            entity.setCompany(c);
        }

        if (this.departement != null) {
            entity.setDepartement(this.departement);
        } else if (this.departementId != null) {
            Departement d = new Departement();
            d.setId(this.departementId);
            entity.setDepartement(d);
        }

        if (this.poste != null) {
            entity.setPoste(this.poste);
        } else if (this.posteId != null) {
            Poste p = new Poste();
            p.setId(this.posteId);
            entity.setPoste(p);
        }

        if (this.categorieEmploye != null) {
            entity.setCategorieEmploye(this.categorieEmploye);
        } else if (this.categorieEmployeId != null) {
            CategorieEmploye ce = new CategorieEmploye();
            ce.setId(this.categorieEmployeId);
            entity.setCategorieEmploye(ce);
        }

        if (this.natureContrat != null) {
            entity.setNatureContrat(this.natureContrat);
        } else if (this.natureContratId != null) {
            NatureContrat nc = new NatureContrat();
            nc.setId(this.natureContratId);
            entity.setNatureContrat(nc);
        }

        if (this.modeDePaiement != null) {
            entity.setModeDePaiement(this.modeDePaiement);
        } else if (this.modeDePaiementId != null) {
            ModeDePaiement mdp = new ModeDePaiement();
            mdp.setId(this.modeDePaiementId);
            entity.setModeDePaiement(mdp);
        }

        if (this.banque != null) {
            entity.setBanque(this.banque);
        } else if (this.banqueId != null) {
            Banque b = new Banque();
            b.setId(this.banqueId);
            entity.setBanque(b);
        }

        if (this.addedBy != null) {
            entity.setAdded_by(this.addedBy);
        } else if (this.addedById != null) {
            User u = new User();
            u.setId(this.addedById);
            entity.setAdded_by(u);
        }

        entity.setDiplome_requis(this.diplome_requis);
        entity.setMissions(this.missions);
        entity.setLieu_execution(this.lieu_execution);
        entity.setNumeroContrat(this.numeroContrat);
        entity.setMouvement_contrat(this.mouvementContrat);
        entity.setType_contrat(this.typeContrat);
        entity.setNumero_compte(this.numeroCompte);
        entity.setDate_debut(this.dateDebut);
        entity.setDate_fin(this.dateFin);
        entity.setDuree(this.duree);
        entity.setDebut_essai(this.debutEssai);
        entity.setFin_essai(this.finEssai);
        entity.setDuree_essai(this.dureeEssai);
        entity.setDateEmbauche(this.dateEmbauche);
        entity.setDuree_contrat(this.dureeContrat);
        entity.setAib_contrat_employe(this.aibContratEmploye);
        entity.setCaution_contrat_employe(this.cautionContratEmploye);
        entity.setTransfert_contrat_employe(this.transfertContratEmploye);
        entity.setStatus_contrat(this.statusContrat);
        entity.setArretContrat(this.arretContrat);
        entity.setDate_arret_contrat(this.dateArretContrat);
        entity.setMotif_arret_contrat(this.motifArretContrat);

        entity.setSalaire_brut(this.salaire_brut != null ? this.salaire_brut : 0.0);
        entity.setSalaire_base(this.salaire_base != null ? this.salaire_base : 0.0);

        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);

        // ============================================
        // MAPPING NOUVEAUX ATTRIBUTS (toEntity)
        // ============================================
        entity.setNatureJuridiqueEmployeur(this.natureJuridiqueEmployeur);
        entity.setAncienneSituation(this.ancienneSituation);
        entity.setNouvelleSituation(this.nouvelleSituation);
        entity.setCommentaire(this.commentaire);
        entity.setHoraireTravail(this.horaireTravail);

        return entity;
    }

    // ============================================
    // GETTERS & SETTERS EXISTANTS (conservés)
    // ============================================


    public String getHoraireTravail() {
        return horaireTravail;
    }

    public void setHoraireTravail(String horaireTravail) {
        this.horaireTravail = horaireTravail;
    }
    public String getNumeroContrat() {
        return numeroContrat;
    }

    public void setNumeroContrat(String numeroContrat) {
        this.numeroContrat = numeroContrat;
    }

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

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Long departementId) {
        this.departementId = departementId;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public DepartementDTO getDepartementDTO() {
        return departementDTO;
    }

    public void setDepartementDTO(DepartementDTO departementDTO) {
        this.departementDTO = departementDTO;
    }

    public Long getPosteId() {
        return posteId;
    }

    public void setPosteId(Long posteId) {
        this.posteId = posteId;
    }

    public boolean isArretContrat() {
        return arretContrat;
    }

    public void setArretContrat(boolean arretContrat) {
        this.arretContrat = arretContrat;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Long getCategorieEmployeId() {
        return categorieEmployeId;
    }

    public void setCategorieEmployeId(Long categorieEmployeId) {
        this.categorieEmployeId = categorieEmployeId;
    }

    public CategorieEmploye getCategorieEmploye() {
        return categorieEmploye;
    }

    public void setCategorieEmploye(CategorieEmploye categorieEmploye) {
        this.categorieEmploye = categorieEmploye;
    }

    public Long getNatureContratId() {
        return natureContratId;
    }

    public void setNatureContratId(Long natureContratId) {
        this.natureContratId = natureContratId;
    }

    public NatureContrat getNatureContrat() {
        return natureContrat;
    }

    public void setNatureContrat(NatureContrat natureContrat) {
        this.natureContrat = natureContrat;
    }

    public Long getModeDePaiementId() {
        return modeDePaiementId;
    }

    public void setModeDePaiementId(Long modeDePaiementId) {
        this.modeDePaiementId = modeDePaiementId;
    }

    public ModeDePaiement getModeDePaiement() {
        return modeDePaiement;
    }

    public void setModeDePaiement(ModeDePaiement modeDePaiement) {
        this.modeDePaiement = modeDePaiement;
    }

    public Long getBanqueId() {
        return banqueId;
    }

    public void setBanqueId(Long banqueId) {
        this.banqueId = banqueId;
    }

    public Banque getBanque() {
        return banque;
    }

    public void setBanque(Banque banque) {
        this.banque = banque;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public String getDiplome_requis() {
        return diplome_requis;
    }

    public void setDiplome_requis(String diplome_requis) {
        this.diplome_requis = diplome_requis;
    }

    public String getMissions() {
        return missions;
    }

    public void setMissions(String missions) {
        this.missions = missions;
    }

    public String getLieu_execution() {
        return lieu_execution;
    }

    public void setLieu_execution(String lieu_execution) {
        this.lieu_execution = lieu_execution;
    }

    public String getMouvementContrat() {
        return mouvementContrat;
    }

    public void setMouvementContrat(String mouvementContrat) {
        this.mouvementContrat = mouvementContrat;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public LocalDate getDebutEssai() {
        return debutEssai;
    }

    public void setDebutEssai(LocalDate debutEssai) {
        this.debutEssai = debutEssai;
    }

    public LocalDate getFinEssai() {
        return finEssai;
    }

    public void setFinEssai(LocalDate finEssai) {
        this.finEssai = finEssai;
    }

    public String getDureeEssai() {
        return dureeEssai;
    }

    public void setDureeEssai(String dureeEssai) {
        this.dureeEssai = dureeEssai;
    }

    public String getDureeContrat() {
        return dureeContrat;
    }

    public void setDureeContrat(String dureeContrat) {
        this.dureeContrat = dureeContrat;
    }

    public double getAibContratEmploye() {
        return aibContratEmploye;
    }

    public void setAibContratEmploye(double aibContratEmploye) {
        this.aibContratEmploye = aibContratEmploye;
    }

    public double getCautionContratEmploye() {
        return cautionContratEmploye;
    }

    public void setCautionContratEmploye(double cautionContratEmploye) {
        this.cautionContratEmploye = cautionContratEmploye;
    }

    public double getTransfertContratEmploye() {
        return transfertContratEmploye;
    }

    public void setTransfertContratEmploye(double transfertContratEmploye) {
        this.transfertContratEmploye = transfertContratEmploye;
    }

    public String getStatusContrat() {
        return statusContrat;
    }

    public void setStatusContrat(String statusContrat) {
        this.statusContrat = statusContrat;
    }

    public LocalDate getDateArretContrat() {
        return dateArretContrat;
    }

    public void setDateArretContrat(LocalDate dateArretContrat) {
        this.dateArretContrat = dateArretContrat;
    }

    public String getMotifArretContrat() {
        return motifArretContrat;
    }

    public void setMotifArretContrat(String motifArretContrat) {
        this.motifArretContrat = motifArretContrat;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Double getSalaire_brut() {
        return salaire_brut != null ? salaire_brut : 0.0;
    }

    public void setSalaire_brut(double salaire_brut) {
        this.salaire_brut = salaire_brut;
    }

    public Double getSalaire_base() {
        return salaire_base;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public void setSalaire_base(double salaire_base) {
        this.salaire_base = salaire_base;
    }

    public void setSalaire_brut(Double salaire_brut) {
        this.salaire_brut = salaire_brut;
    }

    public void setSalaire_base(Double salaire_base) {
        this.salaire_base = salaire_base;
    }

    // ============================================
    // GETTERS & SETTERS NOUVEAUX ATTRIBUTS
    // ============================================

    public String getNatureJuridiqueEmployeur() {
        return natureJuridiqueEmployeur;
    }

    public void setNatureJuridiqueEmployeur(String natureJuridiqueEmployeur) {
        this.natureJuridiqueEmployeur = natureJuridiqueEmployeur;
    }

    public String getAncienneSituation() {
        return ancienneSituation;
    }

    public void setAncienneSituation(String ancienneSituation) {
        this.ancienneSituation = ancienneSituation;
    }

    public String getNouvelleSituation() {
        return nouvelleSituation;
    }

    public void setNouvelleSituation(String nouvelleSituation) {
        this.nouvelleSituation = nouvelleSituation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}