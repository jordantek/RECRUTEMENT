package com.tpc.tpcgestpaie.localapp.dto.bulletin;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class BulletinPaieSageGenerateDTO {

    @JsonProperty("id_bulletin_paie")
    private Long idBulletin;

    @JsonProperty("autre_avantange_bulletin_paie")
    private Double autreAvantage;

    @JsonProperty("autre_retenue_bulletin_paie")
    private Double autreRetenue;

    @JsonProperty("mois_bulletin_paie")
    private String mois;

    @JsonProperty("montant_cnss_bulletin_paie")
    private Double montantCnss;

    @JsonProperty("montant_cnss_employeur_bulletin_paie")
    private Double montantCnssEmployeur;

    @JsonProperty("montant_ipts_bulletin_paie")
    private Double montantIpts;

    @JsonProperty("montant_vps_bulletin_paie")
    private Double montantVps;

    @JsonProperty("net_a_payer_bulletin_paie")
    private Double netAPayer;

    @JsonProperty("nombre_enfant")
    private Integer nombreEnfant;

    @JsonProperty("nombreJourOuHeureTravail")
    private Double nombreJourOuHeureTravail;

    @JsonProperty("salaire_brut_bulletin_paie")
    private Double salaireBrut;

    @JsonProperty("salaire_net_bulletin_paie")
    private Double salaireNet;

    @JsonProperty("total_charge_patronale_bulletin_paie")
    private Double totalChargePatronale;

    @JsonProperty("total_retenue_bulletin_paie")
    private Double totalRetenue;

    @JsonProperty("id_contrat_employe")
    private Long idContratEmploye;

    @JsonProperty("id_employe")
    private Long idEmploye;

    @JsonProperty("id_entreprise")
    private Long idEntreprise;

    @JsonProperty("salaire_brut_arrondi_bulletin_paie")
    private Double salaireBrutArrondi;

    @JsonProperty("date_Calcul_salaire")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCalculSalaire;

    @JsonProperty("numero_compte_employe")
    private String numeroCompteEmploye;

    @JsonProperty("id_banque")
    private Long idBanque;

    @JsonProperty("montant_aib_bulletin_paie")
    private Double montantAib;

    @JsonProperty("id_departement")
    private Long idDepartement;

    @JsonProperty("created_date")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonProperty("last_update")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdate;

    @JsonProperty("id_user")
    private Long idUser;

    @JsonProperty("signataire")
    private String signataire;

    @JsonProperty("conge_pris")
    private Double congePris;

    @JsonProperty("solde_conge")
    private Double soldeConge;

    // Getters et Setters pour tous les champs
    // ...

    public Long getIdBulletin() {
        return idBulletin;
    }

    public void setIdBulletin(Long idBulletin) {
        this.idBulletin = idBulletin;
    }

    public Double getAutreAvantage() {
        return autreAvantage;
    }

    public void setAutreAvantage(Double autreAvantage) {
        this.autreAvantage = autreAvantage;
    }

    public Double getAutreRetenue() {
        return autreRetenue;
    }

    public void setAutreRetenue(Double autreRetenue) {
        this.autreRetenue = autreRetenue;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public Double getMontantCnss() {
        return montantCnss;
    }

    public void setMontantCnss(Double montantCnss) {
        this.montantCnss = montantCnss;
    }

    public Double getMontantCnssEmployeur() {
        return montantCnssEmployeur;
    }

    public void setMontantCnssEmployeur(Double montantCnssEmployeur) {
        this.montantCnssEmployeur = montantCnssEmployeur;
    }

    public Double getMontantIpts() {
        return montantIpts;
    }

    public void setMontantIpts(Double montantIpts) {
        this.montantIpts = montantIpts;
    }

    public Double getMontantVps() {
        return montantVps;
    }

    public void setMontantVps(Double montantVps) {
        this.montantVps = montantVps;
    }

    public Double getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(Double netAPayer) {
        this.netAPayer = netAPayer;
    }

    public Integer getNombreEnfant() {
        return nombreEnfant;
    }

    public void setNombreEnfant(Integer nombreEnfant) {
        this.nombreEnfant = nombreEnfant;
    }

    public Double getNombreJourOuHeureTravail() {
        return nombreJourOuHeureTravail;
    }

    public void setNombreJourOuHeureTravail(Double nombreJourOuHeureTravail) {
        this.nombreJourOuHeureTravail = nombreJourOuHeureTravail;
    }

    public Double getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(Double salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public Double getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(Double salaireNet) {
        this.salaireNet = salaireNet;
    }

    public Double getTotalChargePatronale() {
        return totalChargePatronale;
    }

    public void setTotalChargePatronale(Double totalChargePatronale) {
        this.totalChargePatronale = totalChargePatronale;
    }

    public Double getTotalRetenue() {
        return totalRetenue;
    }

    public void setTotalRetenue(Double totalRetenue) {
        this.totalRetenue = totalRetenue;
    }

    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public Long getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Long idEmploye) {
        this.idEmploye = idEmploye;
    }

    public Long getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(Long idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public Double getSalaireBrutArrondi() {
        return salaireBrutArrondi;
    }

    public void setSalaireBrutArrondi(Double salaireBrutArrondi) {
        this.salaireBrutArrondi = salaireBrutArrondi;
    }

    public LocalDateTime getDateCalculSalaire() {
        return dateCalculSalaire;
    }

    public void setDateCalculSalaire(LocalDateTime dateCalculSalaire) {
        this.dateCalculSalaire = dateCalculSalaire;
    }

    public String getNumeroCompteEmploye() {
        return numeroCompteEmploye;
    }

    public void setNumeroCompteEmploye(String numeroCompteEmploye) {
        this.numeroCompteEmploye = numeroCompteEmploye;
    }

    public Long getIdBanque() {
        return idBanque;
    }

    public void setIdBanque(Long idBanque) {
        this.idBanque = idBanque;
    }

    public Double getMontantAib() {
        return montantAib;
    }

    public void setMontantAib(Double montantAib) {
        this.montantAib = montantAib;
    }

    public Long getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Long idDepartement) {
        this.idDepartement = idDepartement;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getSignataire() {
        return signataire;
    }

    public void setSignataire(String signataire) {
        this.signataire = signataire;
    }

    public Double getCongePris() {
        return congePris;
    }

    public void setCongePris(Double congePris) {
        this.congePris = congePris;
    }

    public Double getSoldeConge() {
        return soldeConge;
    }

    public void setSoldeConge(Double soldeConge) {
        this.soldeConge = soldeConge;
    }
}
