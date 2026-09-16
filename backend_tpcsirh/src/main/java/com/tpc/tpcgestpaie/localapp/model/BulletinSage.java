package com.tpc.tpcgestpaie.localapp.model;

import java.time.LocalDateTime;

public class BulletinSage {

    private Long id_bulletin_paie;
    private Double autre_avantange_bulletin_paie;
    private Double autre_retenue_bulletin_paie;
    private String mois_bulletin_paie;
    private Double montant_cnss_bulletin_paie;
    private Double montant_cnss_employeur_bulletin_paie;
    private Double montant_ipts_bulletin_paie;
    private Double montant_vps_bulletin_paie;
    private Double net_a_payer_bulletin_paie;
    private Integer nombre_enfant;
    private Double nombreJourOuHeureTravail;
    private Double salaire_brut_bulletin_paie;
    private Double salaire_net_bulletin_paie;
    private Double total_charge_patronale_bulletin_paie;
    private Double total_retenue_bulletin_paie;
    private Long id_contrat_employe;
    private Long id_employe;
    private Long id_entreprise;
    private Double salaire_brut_arrondi_bulletin_paie;
    private String date_Calcul_salaire;
    private String numero_compte_employe;
    private Long id_banque;
    private Double montant_aib_bulletin_paie;
    private Long id_departement;
    private LocalDateTime created_date;
    private LocalDateTime last_update;
    private Long id_user;
    private String signataire;
    private Double conge_pris;
    private Double solde_conge;

    // Getters et Setters
    public Long getId_bulletin_paie() { return id_bulletin_paie; }
    public void setId_bulletin_paie(Long id_bulletin_paie) { this.id_bulletin_paie = id_bulletin_paie; }

    public Double getAutre_avantange_bulletin_paie() { return autre_avantange_bulletin_paie; }
    public void setAutre_avantange_bulletin_paie(Double autre_avantange_bulletin_paie) { this.autre_avantange_bulletin_paie = autre_avantange_bulletin_paie; }

    public Double getAutre_retenue_bulletin_paie() { return autre_retenue_bulletin_paie; }
    public void setAutre_retenue_bulletin_paie(Double autre_retenue_bulletin_paie) { this.autre_retenue_bulletin_paie = autre_retenue_bulletin_paie; }

    public String getMois_bulletin_paie() { return mois_bulletin_paie; }
    public void setMois_bulletin_paie(String mois_bulletin_paie) { this.mois_bulletin_paie = mois_bulletin_paie; }

    public Double getMontant_cnss_bulletin_paie() { return montant_cnss_bulletin_paie; }
    public void setMontant_cnss_bulletin_paie(Double montant_cnss_bulletin_paie) { this.montant_cnss_bulletin_paie = montant_cnss_bulletin_paie; }

    public Double getMontant_cnss_employeur_bulletin_paie() { return montant_cnss_employeur_bulletin_paie; }
    public void setMontant_cnss_employeur_bulletin_paie(Double montant_cnss_employeur_bulletin_paie) { this.montant_cnss_employeur_bulletin_paie = montant_cnss_employeur_bulletin_paie; }

    public Double getMontant_ipts_bulletin_paie() { return montant_ipts_bulletin_paie; }
    public void setMontant_ipts_bulletin_paie(Double montant_ipts_bulletin_paie) { this.montant_ipts_bulletin_paie = montant_ipts_bulletin_paie; }

    public Double getMontant_vps_bulletin_paie() { return montant_vps_bulletin_paie; }
    public void setMontant_vps_bulletin_paie(Double montant_vps_bulletin_paie) { this.montant_vps_bulletin_paie = montant_vps_bulletin_paie; }

    public Double getNet_a_payer_bulletin_paie() { return net_a_payer_bulletin_paie; }
    public void setNet_a_payer_bulletin_paie(Double net_a_payer_bulletin_paie) { this.net_a_payer_bulletin_paie = net_a_payer_bulletin_paie; }

    public Integer getNombre_enfant() { return nombre_enfant; }
    public void setNombre_enfant(Integer nombre_enfant) { this.nombre_enfant = nombre_enfant; }

    public Double getNombreJourOuHeureTravail() { return nombreJourOuHeureTravail; }
    public void setNombreJourOuHeureTravail(Double nombreJourOuHeureTravail) { this.nombreJourOuHeureTravail = nombreJourOuHeureTravail; }

    public Double getSalaire_brut_bulletin_paie() { return salaire_brut_bulletin_paie; }
    public void setSalaire_brut_bulletin_paie(Double salaire_brut_bulletin_paie) { this.salaire_brut_bulletin_paie = salaire_brut_bulletin_paie; }

    public Double getSalaire_net_bulletin_paie() { return salaire_net_bulletin_paie; }
    public void setSalaire_net_bulletin_paie(Double salaire_net_bulletin_paie) { this.salaire_net_bulletin_paie = salaire_net_bulletin_paie; }

    public Double getTotal_charge_patronale_bulletin_paie() { return total_charge_patronale_bulletin_paie; }
    public void setTotal_charge_patronale_bulletin_paie(Double total_charge_patronale_bulletin_paie) { this.total_charge_patronale_bulletin_paie = total_charge_patronale_bulletin_paie; }

    public Double getTotal_retenue_bulletin_paie() { return total_retenue_bulletin_paie; }
    public void setTotal_retenue_bulletin_paie(Double total_retenue_bulletin_paie) { this.total_retenue_bulletin_paie = total_retenue_bulletin_paie; }

    public Long getId_contrat_employe() { return id_contrat_employe; }
    public void setId_contrat_employe(Long id_contrat_employe) { this.id_contrat_employe = id_contrat_employe; }

    public Long getId_employe() { return id_employe; }
    public void setId_employe(Long id_employe) { this.id_employe = id_employe; }

    public Long getId_entreprise() { return id_entreprise; }
    public void setId_entreprise(Long id_entreprise) { this.id_entreprise = id_entreprise; }

    public Double getSalaire_brut_arrondi_bulletin_paie() { return salaire_brut_arrondi_bulletin_paie; }
    public void setSalaire_brut_arrondi_bulletin_paie(Double salaire_brut_arrondi_bulletin_paie) { this.salaire_brut_arrondi_bulletin_paie = salaire_brut_arrondi_bulletin_paie; }

    public String getDate_Calcul_salaire() { return date_Calcul_salaire; }
    public void setDate_Calcul_salaire(String date_Calcul_salaire) { this.date_Calcul_salaire = date_Calcul_salaire; }

    public String getNumero_compte_employe() { return numero_compte_employe; }
    public void setNumero_compte_employe(String numero_compte_employe) { this.numero_compte_employe = numero_compte_employe; }

    public Long getId_banque() { return id_banque; }
    public void setId_banque(Long id_banque) { this.id_banque = id_banque; }

    public Double getMontant_aib_bulletin_paie() { return montant_aib_bulletin_paie; }
    public void setMontant_aib_bulletin_paie(Double montant_aib_bulletin_paie) { this.montant_aib_bulletin_paie = montant_aib_bulletin_paie; }

    public Long getId_departement() { return id_departement; }
    public void setId_departement(Long id_departement) { this.id_departement = id_departement; }

    public LocalDateTime getCreated_date() { return created_date; }
    public void setCreated_date(LocalDateTime created_date) { this.created_date = created_date; }

    public LocalDateTime getLast_update() { return last_update; }
    public void setLast_update(LocalDateTime last_update) { this.last_update = last_update; }

    public Long getId_user() { return id_user; }
    public void setId_user(Long id_user) { this.id_user = id_user; }

    public String getSignataire() { return signataire; }
    public void setSignataire(String signataire) { this.signataire = signataire; }

    public Double getConge_pris() { return conge_pris; }
    public void setConge_pris(Double conge_pris) { this.conge_pris = conge_pris; }

    public Double getSolde_conge() { return solde_conge; }
    public void setSolde_conge(Double solde_conge) { this.solde_conge = solde_conge; }
}
