package com.tpc.tpcgestpaie.localapp.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.model.Company;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CompanyDTO {

    private Long id;
    private String name;
    private String nss;
    private BigDecimal rss;
    private String address;
    private String country;
    private String countryCode;
    private String email;
    private String phone;
    private String webSite;
    private String rccm;
    private String ifu;
    private String logo;
    private String directorName;
    private String directorEmail;
    private String directorPhone;
    private Short statusId;
    private BigDecimal tvaVal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long createdBy;
    private Long updatedBy;

    private LocalDate creationDate;
    private Double vps;
    private LocalDate vpsEffectDate;
    private String signatoryName;
    private BigDecimal ca;
    private String modeJouissanceConge;
    private Double nbrJourTravail;
    private Double nbrJourConge;
    private Double heuresParJour;
    private Double heuresParSemaine;
    private Boolean estEntreprisePrincipale;

    // ⭐ NOUVEAUX CHAMPS POUR LA GESTION CLIENT-ENTREPRISES
    private Long clientId;
    private String clientName;
    private Company.LicenseType licenseType;
    private boolean clientCompany;
    private boolean managedCompany;
    private boolean canManageCompanies;
    private boolean standalone;
    private List<CompanyDTO> managedCompanies;
    private int managedCompaniesCount;


    private Set<String> activityAreas;
    private List<Long> banqueIds;

    // Getters et setters existants...


    public List<Long> getBanqueIds() {
        return banqueIds;
    }

    public void setBanqueIds(List<Long> banqueIds) {
        this.banqueIds = banqueIds;
    }

    public Set<String> getActivityAreas() {
        return activityAreas;
    }

    public void setActivityAreas(Set<String> activityAreas) {
        this.activityAreas = activityAreas;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTvaVal() {
        return tvaVal;
    }

    public void setTvaVal(BigDecimal tvaVal) {
        this.tvaVal = tvaVal;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public String getDirectorPhone() {
        return directorPhone;
    }

    public void setDirectorPhone(String directorPhone) {
        this.directorPhone = directorPhone;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIfu() {
        return ifu;
    }

    public void setIfu(String ifu) {
        this.ifu = ifu;
    }

    public String getRccm() {
        return rccm;
    }

    public void setRccm(String rccm) {
        this.rccm = rccm;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getRss() {
        return rss;
    }

    public void setRss(BigDecimal rss) {
        this.rss = rss;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Double getVps() {
        return vps != null ? vps : 0.0;
    }

    public void setVps(Double vps) {
        this.vps = vps;
    }

    public LocalDate getVpsEffectDate() {
        return vpsEffectDate;
    }

    public void setVpsEffectDate(LocalDate vpsEffectDate) {
        this.vpsEffectDate = vpsEffectDate;
    }

    public String getSignatoryName() {
        return signatoryName;
    }

    public void setSignatoryName(String signatoryName) {
        this.signatoryName = signatoryName;
    }

    public BigDecimal getCa() {
        return ca;
    }

    public void setCa(BigDecimal ca) {
        this.ca = ca;
    }

    public String getModeJouissanceConge() {
        return modeJouissanceConge;
    }

    public void setModeJouissanceConge(String modeJouissanceConge) {
        this.modeJouissanceConge = modeJouissanceConge;
    }

    public Double getNbrJourTravail() {
        return nbrJourTravail;
    }

    public void setNbrJourTravail(Double nbrJourTravail) {
        this.nbrJourTravail = nbrJourTravail;
    }

    public Double getNbrJourConge() {
        return nbrJourConge;
    }

    public void setNbrJourConge(Double nbrJourConge) {
        this.nbrJourConge = nbrJourConge;
    }

    public Double getHeuresParJour() {
        return heuresParJour;
    }

    public void setHeuresParJour(Double heuresParJour) {
        this.heuresParJour = heuresParJour;
    }

    public Double getHeuresParSemaine() {
        return heuresParSemaine;
    }

    public void setHeuresParSemaine(Double heuresParSemaine) {
        this.heuresParSemaine = heuresParSemaine;
    }

    public Boolean getEstEntreprisePrincipale() {
        return estEntreprisePrincipale;
    }

    public void setEstEntreprisePrincipale(Boolean estEntreprisePrincipale) {
        this.estEntreprisePrincipale = estEntreprisePrincipale;
    }

    // ⭐ NOUVEAUX GETTERS/SETTERS POUR LA GESTION CLIENT
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Company.LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(Company.LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public boolean isClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(boolean clientCompany) {
        this.clientCompany = clientCompany;
    }

    public boolean isManagedCompany() {
        return managedCompany;
    }

    public void setManagedCompany(boolean managedCompany) {
        this.managedCompany = managedCompany;
    }

    public boolean isCanManageCompanies() {
        return canManageCompanies;
    }

    public void setCanManageCompanies(boolean canManageCompanies) {
        this.canManageCompanies = canManageCompanies;
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public List<CompanyDTO> getManagedCompanies() {
        return managedCompanies;
    }

    public void setManagedCompanies(List<CompanyDTO> managedCompanies) {
        this.managedCompanies = managedCompanies;
    }

    public int getManagedCompaniesCount() {
        return managedCompaniesCount;
    }

    public void setManagedCompaniesCount(int managedCompaniesCount) {
        this.managedCompaniesCount = managedCompaniesCount;
    }

    // ⭐ MÉTHODE DE MAPPING AMÉLIORÉE
    public static CompanyDTO fromEntity(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDTO dto = new CompanyDTO();

        // Champs de base
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setNss(company.getNss());
        dto.setRss(company.getRss());
        dto.setAddress(company.getAddress());
        dto.setCountry(company.getCountry());
        dto.setCountryCode(company.getCountryCode());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setWebSite(company.getWebSite());
        dto.setRccm(company.getRccm());
        dto.setIfu(company.getIfu());
        dto.setLogo(company.getLogo());
        dto.setDirectorName(company.getDirectorName());
        dto.setDirectorEmail(company.getDirectorEmail());
        dto.setDirectorPhone(company.getDirectorPhone());
        dto.setStatusId(company.getStatusId());
        dto.setTvaVal(company.getTvaVal());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        dto.setDeletedAt(company.getDeletedAt());
        dto.setCreatedBy(company.getCreatedBy());
        dto.setUpdatedBy(company.getUpdatedBy());

        dto.setCreationDate(company.getCreationDate());
        dto.setVps(company.getVps());
        dto.setVpsEffectDate(company.getVpsEffectDate());
        dto.setSignatoryName(company.getSignatoryName());
        dto.setCa(company.getCa());
        dto.setModeJouissanceConge(company.getModeJouissanceConge());
        dto.setNbrJourTravail(company.getNbrJourTravail());
        dto.setNbrJourConge(company.getNbrJourConge());
        dto.setHeuresParJour(company.getHeuresParJour());
        dto.setHeuresParSemaine(company.getHeuresParSemaine());
        dto.setEstEntreprisePrincipale(company.getEstEntreprisePrincipale());

        // ⭐ NOUVEAUX CHAMPS POUR LA GESTION CLIENT
        dto.setLicenseType(company.getLicenseType());
        dto.setClientCompany(company.isClientCompany());
        dto.setManagedCompany(company.isManagedCompany());
        dto.setCanManageCompanies(company.canManageCompanies());
        dto.setStandalone(company.isStandalone());
        dto.setManagedCompaniesCount(company.getManagedCompanies().size());

        // Informations du client si c'est une entreprise gérée
        if (company.isManagedCompany() && company.getClient() != null) {
            dto.setClientId(company.getClient().getId());
            dto.setClientName(company.getClient().getName());
        }

        // Zones d'activité
        if (company.getActivityAreas() != null) {
            dto.setActivityAreas(company.getActivityAreas().stream()
                    .map(activityArea -> activityArea.getName())
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    // ⭐ MÉTHODE POUR MAPPAGE COMPLET AVEC ENTREPRISES GÉRÉES
    public static CompanyDTO fromEntityWithManagedCompanies(Company company) {
        CompanyDTO dto = fromEntity(company);

        if (dto != null && company.canManageCompanies()) {
            // Mapper les entreprises gérées
            List<CompanyDTO> managedCompaniesDTOs = company.getManagedCompanies().stream()
                    .map(CompanyDTO::fromEntity)
                    .collect(Collectors.toList());
            dto.setManagedCompanies(managedCompaniesDTOs);
        }

        return dto;
    }

    // ⭐ MÉTHODE POUR CRÉATION (sans ID et dates)
    public static CompanyDTO forCreation(Company company) {
        CompanyDTO dto = new CompanyDTO();

        dto.setName(company.getName());
        dto.setNss(company.getNss());
        dto.setRss(company.getRss());
        dto.setAddress(company.getAddress());
        dto.setCountry(company.getCountry());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setWebSite(company.getWebSite());
        dto.setRccm(company.getRccm());
        dto.setIfu(company.getIfu());
        dto.setLogo(company.getLogo());
        dto.setDirectorName(company.getDirectorName());
        dto.setDirectorEmail(company.getDirectorEmail());
        dto.setDirectorPhone(company.getDirectorPhone());
        dto.setStatusId(company.getStatusId());
        dto.setTvaVal(company.getTvaVal());

        dto.setCreationDate(company.getCreationDate());
        dto.setVps(company.getVps());
        dto.setVpsEffectDate(company.getVpsEffectDate());
        dto.setSignatoryName(company.getSignatoryName());
        dto.setCa(company.getCa());
        dto.setModeJouissanceConge(company.getModeJouissanceConge());
        dto.setNbrJourTravail(company.getNbrJourTravail());
        dto.setNbrJourConge(company.getNbrJourConge());
        dto.setHeuresParJour(company.getHeuresParJour());
        dto.setHeuresParSemaine(company.getHeuresParSemaine());
        dto.setEstEntreprisePrincipale(company.getEstEntreprisePrincipale());

        // Gestion client
        dto.setLicenseType(company.getLicenseType());
        dto.setClientId(company.getClient() != null ? company.getClient().getId() : null);

        return dto;
    }
}