package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccidentTravailDTO {

    private Long id;
    private Long contratEmployeId;
    private Long employeId;
    private EmployeDTO employe;
    private Long companyId;
    private LocalDate dateAccident;
    private LocalDate dateDeclaration;
    private String effetAccident;
    private String action;
    private BigInteger depense;
    private Long addedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Setters & Getters

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

    public LocalDate getDateAccident() {
        return dateAccident;
    }

    public void setDateAccident(LocalDate dateAccident) {
        this.dateAccident = dateAccident;
    }

    public LocalDate getDateDeclaration() {
        return dateDeclaration;
    }

    public void setDateDeclaration(LocalDate dateDeclaration) {
        this.dateDeclaration = dateDeclaration;
    }

    public String getEffetAccident() {
        return effetAccident;
    }

    public void setEffetAccident(String effetAccident) {
        this.effetAccident = effetAccident;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigInteger getDepense() {
        return depense;
    }

    public void setDepense(BigInteger depense) {
        this.depense = depense;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
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


}