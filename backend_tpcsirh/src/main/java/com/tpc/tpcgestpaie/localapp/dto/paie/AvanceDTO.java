package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class AvanceDTO {

    private Long id;
    private Long contratEmployeId;
    private Long employeId;
    private EmployeDTO employe;
    private Long companyId;
    private String  moisDemarrage;

    private String  moisFin;

    private int dureeAvance;

    private String observation;

    private BigDecimal montantTotal;
    private BigDecimal montantMensuel;

    private boolean statut;

    private Long addedById;


    // Méthodes utilitaires pour la conversion
    public YearMonth getMoisDemarrageAsYearMonth() {
        return moisDemarrage != null ? YearMonth.parse(moisDemarrage) : null;
    }

    public YearMonth getMoisFinAsYearMonth() {
        return moisFin != null ? YearMonth.parse(moisFin) : null;
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String  getMoisDemarrage() {
        return moisDemarrage;
    }

    public void setMoisDemarrage(String  moisDemarrage) {
        this.moisDemarrage = moisDemarrage;
    }

    public String  getMoisFin() {
        return moisFin;
    }

    public void setMoisFin(String  moisFin) {
        this.moisFin = moisFin;
    }

    public int getDureeAvance() {
        return dureeAvance;
    }

    public void setDureeAvance(int dureeAvance) {
        this.dureeAvance = dureeAvance;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getMontantMensuel() {
        return montantMensuel;
    }

    public void setMontantMensuel(BigDecimal montantMensuel) {
        this.montantMensuel = montantMensuel;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    // --- Conversion depuis l'entité ---
    public static AvanceDTO fromEntity(Avance entity) {
        AvanceDTO dto = new AvanceDTO();
        dto.setId(entity.getId());
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));

        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        dto.setMoisDemarrage(entity.getMoisDemarrage() != null
                ? entity.getMoisDemarrage().format(formatter)
                : null);

        dto.setMoisFin(entity.getMoisFin() != null
                ? entity.getMoisFin().format(formatter)
                : null);
        dto.setMontantTotal(entity.getMontantTotal());
        dto.setDureeAvance(entity.getDureeAvance());
        dto.setMontantMensuel(entity.getMontantMensuel());
        dto.setStatut(entity.isStatut());
        dto.setObservation(entity.getObservation());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        return dto;
    }

    // --- Conversion vers l'entité ---
    public Avance toEntity(
            ContratEmploye contratEmploye,
            Employe employe,
            Company company,
            User addedBy
    ) {
        Avance entity = new Avance();
        entity.setId(this.id);
        entity.setContratEmploye(contratEmploye);
        entity.setEmploye(employe);
        entity.setCompany(company);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        entity.setMoisDemarrage(this.moisDemarrage != null
                ? YearMonth.parse(this.moisDemarrage, formatter)
                : null);

        entity.setMoisFin(this.moisFin != null
                ? YearMonth.parse(this.moisFin, formatter)
                : null);

        entity.setMontantTotal(this.montantTotal);
        entity.setDureeAvance(this.dureeAvance);
        entity.setMontantMensuel(this.montantMensuel);
        entity.setStatut(this.statut);
        entity.setObservation(observation);
        entity.setAdded_by(addedBy);
        return entity;
    }

    private static EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }
}
