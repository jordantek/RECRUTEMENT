package com.tpc.tpcgestpaie.localapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItsTrancheDTO {
    private Long id;
    private BigDecimal limiteTranche1;
    private BigDecimal limiteTranche2;
    private BigDecimal limiteTranche3;
    private BigDecimal limiteTranche4;

    private Double rateTranche1;
    private Double rateTranche2;
    private Double rateTranche3;
    private Double rateTranche4;

    private Double rateAbattement;

    private Long addedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
    // ... (même style que ParametrePaieDTO)


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLimiteTranche1() {
        return limiteTranche1;
    }

    public void setLimiteTranche1(BigDecimal limiteTranche1) {
        this.limiteTranche1 = limiteTranche1;
    }

    public BigDecimal getLimiteTranche2() {
        return limiteTranche2;
    }

    public void setLimiteTranche2(BigDecimal limiteTranche2) {
        this.limiteTranche2 = limiteTranche2;
    }

    public BigDecimal getLimiteTranche3() {
        return limiteTranche3;
    }

    public void setLimiteTranche3(BigDecimal limiteTranche3) {
        this.limiteTranche3 = limiteTranche3;
    }

    public BigDecimal getLimiteTranche4() {
        return limiteTranche4;
    }

    public void setLimiteTranche4(BigDecimal limiteTranche4) {
        this.limiteTranche4 = limiteTranche4;
    }

    public Double getRateTranche1() {
        return rateTranche1;
    }

    public void setRateTranche1(Double rateTranche1) {
        this.rateTranche1 = rateTranche1;
    }

    public Double getRateTranche2() {
        return rateTranche2;
    }

    public void setRateTranche2(Double rateTranche2) {
        this.rateTranche2 = rateTranche2;
    }

    public Double getRateTranche3() {
        return rateTranche3;
    }

    public void setRateTranche3(Double rateTranche3) {
        this.rateTranche3 = rateTranche3;
    }

    public Double getRateTranche4() {
        return rateTranche4;
    }

    public void setRateTranche4(Double rateTranche4) {
        this.rateTranche4 = rateTranche4;
    }

    public Double getRateAbattement() {
        return rateAbattement;
    }

    public void setRateAbattement(Double rateAbattement) {
        this.rateAbattement = rateAbattement;
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
