package com.tpc.tpcgestpaie.localapp.dto.setup;

import com.tpc.tpcgestpaie.localapp.model.Company;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LicenseConfig {

    private Company.LicenseType licenseType;
    private Integer durationMonths;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private InstallationType installationType;
    // Ajouter cet enum interne ou utiliser celui de l'entité
    public enum InstallationType {
        LOCAL,   // Installation on-premise
        ONLINE   // Installation SaaS/Cloud
    }

    public Company.LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(Company.LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Getter et Setter
    public InstallationType getInstallationType() {
        return installationType;
    }

    public void setInstallationType(InstallationType installationType) {
        this.installationType = installationType;
    }
}