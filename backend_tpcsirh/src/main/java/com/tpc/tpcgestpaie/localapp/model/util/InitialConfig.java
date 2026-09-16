package com.tpc.tpcgestpaie.localapp.model.util;

import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
// Element calcul salaire
@Table(name = "initial_config")
public class InitialConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateInitialConfig;
    private boolean configStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    // Optionnel: auto-remplir la date si absente
    @PrePersist
    protected void onCreate() {
        if (dateInitialConfig == null) {
            dateInitialConfig = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateInitialConfig() {
        return dateInitialConfig;
    }

    public void setDateInitialConfig(LocalDate dateInitialConfig) {
        this.dateInitialConfig = dateInitialConfig;
    }

    public boolean isConfigStatus() {
        return configStatus;
    }

    public void setConfigStatus(boolean configStatus) {
        this.configStatus = configStatus;
    }
}