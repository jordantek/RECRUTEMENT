package com.tpc.tpcgestpaie.localapp.dto.alertes;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ContratAlerteDTO {
    private Long contratId;
    private String numeroContrat;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private String poste;
    private String departement;
    private String companyName;

    // Dates importantes
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate debutEssai;
    private LocalDate finEssai;

    // Informations d'alerte
    private String typeAlerte; // FIN_ESSAI, FIN_CONTRAT, ANNIVERSAIRE_RECRUTEMENT
    private long joursRestants;
    private LocalDate dateEcheance;
    private String statutContrat;


    public Long getContratId() {
        return contratId;
    }

    public void setContratId(Long contratId) {
        this.contratId = contratId;
    }

    public String getNumeroContrat() {
        return numeroContrat;
    }

    public void setNumeroContrat(String numeroContrat) {
        this.numeroContrat = numeroContrat;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public String getEmployePrenom() {
        return employePrenom;
    }

    public void setEmployePrenom(String employePrenom) {
        this.employePrenom = employePrenom;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(String typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public long getJoursRestants() {
        return joursRestants;
    }

    public void setJoursRestants(long joursRestants) {
        this.joursRestants = joursRestants;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getStatutContrat() {
        return statutContrat;
    }

    public void setStatutContrat(String statutContrat) {
        this.statutContrat = statutContrat;
    }
}