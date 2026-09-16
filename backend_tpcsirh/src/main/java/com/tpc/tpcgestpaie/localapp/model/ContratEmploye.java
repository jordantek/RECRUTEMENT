package com.tpc.tpcgestpaie.localapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Entity
// Element calcul salaire
@Table(name = "contrat_employes")
public class ContratEmploye {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_employe_id")
    private CategorieEmploye categorieEmploye;
    //enum
    private String mouvement_contrat;

    private String diplome_requis;
    private String lieu_execution;
    private String numeroContrat;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String missions;

    @OneToMany(mappedBy = "contratEmploye", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StatutContrat> statuts;
    //enum
    private String type_contrat;
    //Nature Contrat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nature_contrat_id")
    private NatureContrat natureContrat;
    //Mode de paiement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mode_paiement_id")
    private ModeDePaiement modeDePaiement;
    //Banque
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_id")
    private Banque banque;

    private  String numero_compte;

    private LocalDate date_debut;
    private LocalDate date_fin;
    private String duree;

    private  LocalDate debut_essai;
    private  LocalDate fin_essai;
    private String duree_essai;
    //enum [Determiné, indeterminé, lié aux besoins]
    private String duree_contrat;

    private double aib_contrat_employe;

    private double caution_contrat_employe;

    private double transfert_contrat_employe;


    private String status_contrat;

    private boolean arretContrat;
    private LocalDate date_arret_contrat;
    private String motif_arret_contrat;

    @Column(name = "salaire_brut", nullable = true)
    private Double salaire_brut=0.0;

    @Column(name = "salaire_base", nullable = true)
    private Double salaire_base=0.0;

    @OneToMany(mappedBy = "contratEmploye", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ContratEmployeRubrique> rubriques;

    // ⏰ Horaire de travail (format texte libre)
    @Lob
    @Column(name = "horaire_travail", columnDefinition = "TEXT")
    private String horaireTravail;
    // 🏢 Nature juridique de l'employeur (SA, SARL, SAS, etc.)
    @Column(name = "nature_juridique_employeur", length = 100)
    private String natureJuridiqueEmployeur;

    // 📝 Situations pour avenants (stockage JSON ou texte simple)
    @Lob
    @Column(name = "ancienne_situation", columnDefinition = "TEXT")
    private String ancienneSituation;

    @Lob
    @Column(name = "nouvelle_situation", columnDefinition = "TEXT")
    private String nouvelleSituation;

    // 💬 Commentaire libre
    @Lob
    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User added_by;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "date_embauche", nullable = true)
    private LocalDate dateEmbauche;

    // 👤 Supérieur hiérarchique niveau 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sup_n1_id")
    private Employe supN_1;

    // 👤 Supérieur hiérarchique niveau 2
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sup_n2_id")
    private Employe supN_2;

    @Transient
    public Integer getAncienneteEnMois() {
        if (dateEmbauche == null) {
            return null;
        }
        Period p = Period.between(dateEmbauche, LocalDate.now());
        return p.getYears() * 12 + p.getMonths();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<StatutContrat> getStatuts() {
        return statuts;
    }

    public void setStatuts(List<StatutContrat> statuts) {
        this.statuts = statuts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public CategorieEmploye getCategorieEmploye() {
        return categorieEmploye;
    }

    public void setCategorieEmploye(CategorieEmploye categorieEmploye) {
        this.categorieEmploye = categorieEmploye;
    }

     public String getMouvement_contrat() {
        return mouvement_contrat;
    }

    public void setMouvement_contrat(String mouvement_contrat) {
        this.mouvement_contrat = mouvement_contrat;
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

    public String getNumeroContrat() {
        return numeroContrat;
    }

    public void setNumeroContrat(String numeroContrat) {
        this.numeroContrat = numeroContrat;
    }

    public void setLieu_execution(String lieu_execution) {
        this.lieu_execution = lieu_execution;
    }

    public String getType_contrat() {
        return type_contrat;
    }

    public void setType_contrat(String type_contrat) {
        this.type_contrat = type_contrat;
    }

    public NatureContrat getNatureContrat() {
        return natureContrat;
    }

    public void setNatureContrat(NatureContrat natureContrat) {
        this.natureContrat = natureContrat;
    }

    public ModeDePaiement getModeDePaiement() {
        return modeDePaiement;
    }

    public void setModeDePaiement(ModeDePaiement modeDePaiement) {
        this.modeDePaiement = modeDePaiement;
    }

    public Banque getBanque() {
        return banque;
    }

    public void setBanque(Banque banque) {
        this.banque = banque;
    }

    public String getNumero_compte() {
        return numero_compte;
    }

    public void setNumero_compte(String numero_compte) {
        this.numero_compte = numero_compte;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public LocalDate getDebut_essai() {
        return debut_essai;
    }

    public void setDebut_essai(LocalDate debut_essai) {
        this.debut_essai = debut_essai;
    }

    public LocalDate getFin_essai() {
        return fin_essai;
    }

    public void setFin_essai(LocalDate fin_essai) {
        this.fin_essai = fin_essai;
    }

    public String getDuree_essai() {
        return duree_essai;
    }

    public void setDuree_essai(String duree_essai) {
        this.duree_essai = duree_essai;
    }

    public String getDuree_contrat() {
        return duree_contrat;
    }

    public void setDuree_contrat(String duree_contrat) {
        this.duree_contrat = duree_contrat;
    }

    public double getAib_contrat_employe() {
        return aib_contrat_employe;
    }

    public void setAib_contrat_employe(double aib_contrat_employe) {
        this.aib_contrat_employe = aib_contrat_employe;
    }

    public double getCaution_contrat_employe() {
        return caution_contrat_employe;
    }

    public void setCaution_contrat_employe(double caution_contrat_employe) {
        this.caution_contrat_employe = caution_contrat_employe;
    }

    public double getTransfert_contrat_employe() {
        return transfert_contrat_employe;
    }

    public void setTransfert_contrat_employe(double transfert_contrat_employe) {
        this.transfert_contrat_employe = transfert_contrat_employe;
    }

    public String getStatus_contrat() {
        return status_contrat;
    }

    public void setStatus_contrat(String status_contrat) {
        this.status_contrat = status_contrat;
    }

    public boolean isArretContrat() {
        return arretContrat;
    }

    public void setArretContrat(boolean arretContrat) {
        this.arretContrat = arretContrat;
    }

    public LocalDate getDate_arret_contrat() {
        return date_arret_contrat;
    }

    public void setDate_arret_contrat(LocalDate date_arret_contrat) {
        this.date_arret_contrat = date_arret_contrat;
    }

    public String getMotif_arret_contrat() {
        return motif_arret_contrat;
    }

    public void setMotif_arret_contrat(String motif_arret_contrat) {
        this.motif_arret_contrat = motif_arret_contrat;
    }

    public Employe getSupN_1() {
        return supN_1;
    }

    public void setSupN_1(Employe supN_1) {
        this.supN_1 = supN_1;
    }

    public Employe getSupN_2() {
        return supN_2;
    }

    public void setSupN_2(Employe supN_2) {
        this.supN_2 = supN_2;
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

    // Getters et setters

    public List<ContratEmployeRubrique> getRubriques() {
        return rubriques;
    }

    public void setRubriques(List<ContratEmployeRubrique> rubriques) {
        this.rubriques = rubriques;
    }

    public double getSalaire_brut() {
        return salaire_brut;
    }

    public void setSalaire_brut(double salaire_brut) {
        this.salaire_brut = salaire_brut;
    }

    public double getSalaire_base() {
        return salaire_base;
    }

    public void setSalaire_base(double salaire_base) {
        this.salaire_base = salaire_base;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getHoraireTravail() {
        return horaireTravail;
    }

    public void setHoraireTravail(String horaireTravail) {
        this.horaireTravail = horaireTravail;
    }

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