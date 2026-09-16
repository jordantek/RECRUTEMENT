package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "its_tranche")
public class ItsTranche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "unique_key", unique = true, nullable = false)
    private String uniqueKey = "PARAM_UNIQUE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User added_by;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private LocalDateTime deleted_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = created_at;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public User getAdded_by() {
        return added_by;
    }

    public void setAdded_by(User added_by) {
        this.added_by = added_by;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDateTime getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }
}