    package com.tpc.tpcgestpaie.localapp.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.*;


import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Set;

    @Entity
    @Table(name = "companies")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Company {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false)
        private String name;
        private LocalDate creationDate;
        @Column(unique = true)
        private String nss;
        @Column(precision = 15, scale = 2)
        private BigDecimal rss;

        private Double vps=0.0;

        private LocalDate vpsEffectDate;

        @Column(columnDefinition = "TEXT")
        private String address;

        private String country;
        private String countryCode;
        private String email;
        private String phone;
        private String webSite;
        private String rccm;
        private String ifu;
        private String logo;

        // RELATION CLIENT-ENTREPRISES
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "client_id")
        @JsonIgnore
        private Company client;

        @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
        @JsonIgnore
        private List<Company> managedCompanies = new ArrayList<>();

        // GESTION DE LICENCE
        @Enumerated(EnumType.STRING)
        @Column(name = "lt", nullable = false)
        private LicenseType licenseType = LicenseType.SINGLE_COMPANY;

        private String directorName;
        private String directorEmail;
        private String directorPhone;

        private String signatoryName;
        private BigDecimal ca;
        private String modeJouissanceConge;
        private Double nbrJourTravail;
        private Double nbrJourConge;
        private Double heuresParJour;
        private Double heuresParSemaine;

        @Column(name = "est_entreprise_principale", nullable = false)
        private Boolean estEntreprisePrincipale = false;

        private Short statusId;

        @Column(name = "tva_val", precision = 5, scale = 2)
        private BigDecimal tvaVal;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        private Long createdBy;
        private Long updatedBy;

//        @ManyToMany
//        @JoinTable(
//                name = "company_banques",
//                joinColumns = @JoinColumn(name = "company_id"),
//                inverseJoinColumns = @JoinColumn(name = "banque_id")
//        )
//        @JsonIgnore
//        private Set<Banque> banques = new HashSet<>();



        @ManyToMany
        @JoinTable(
                name = "company_activity_areas",
                joinColumns = @JoinColumn(name = "company_id"),
                inverseJoinColumns = @JoinColumn(name = "activity_area_id")
        )

        @JsonIgnore
        private Set<ActivityArea> activityAreas;


        // ENUM
        public enum LicenseType {
            SINGLE_COMPANY,    // Gère uniquement elle-même
            MULTI_COMPANY      // Peut gérer d'autres entreprises
        }

        public boolean arePayrollParametersComplete() {
            return this.rss != null &&
                    this.vps != null &&
                    this.nbrJourTravail != null &&
                    this.nbrJourConge != null &&
                    this.heuresParJour != null &&
                    this.heuresParSemaine != null;
        }

        // MÉTHODES UTILITAIRES
        public boolean isClientCompany() {
            return this.client == null;
        }

        public boolean isManagedCompany() {
            return this.client != null;
        }

        public boolean canManageCompanies() {
            return this.isClientCompany() && this.licenseType == LicenseType.MULTI_COMPANY;
        }

        public boolean isStandalone() {
            return this.isClientCompany() && this.licenseType == LicenseType.SINGLE_COMPANY;
        }

        @JsonIgnore
        public List<Company> getAllAccessibleCompanies() {
            List<Company> companies = new ArrayList<>();
            companies.add(this);
            if (this.canManageCompanies()) {
                companies.addAll(this.managedCompanies);
            }
            return companies;
        }

        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = this.createdAt;
        }

        @PreUpdate
        protected void onUpdate() {
            this.updatedAt = LocalDateTime.now();
        }

        @PreRemove
        protected void onRemove() {
            this.deletedAt = LocalDateTime.now();
        }
        public void setWebsite(String website) {
            this.webSite = website;
        }


        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = (countryCode == null) ? "" : countryCode.toUpperCase();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDate getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(LocalDate creationDate) {
            this.creationDate = creationDate;
        }

        public LocalDate getVpsEffectDate() {
            return vpsEffectDate;
        }

        public void setVpsEffectDate(LocalDate vpsEffectDate) {
            this.vpsEffectDate = vpsEffectDate;
        }

        public Double getVps() {
            return vps;
        }

        public String getSignatoryName() {
            return signatoryName;
        }

        public void setSignatoryName(String signatoryName) {
            this.signatoryName = signatoryName;
        }
        public String getModeJouissanceConge() {
            return modeJouissanceConge;
        }

        public void setModeJouissanceConge(String modeJouissanceConge) {
            this.modeJouissanceConge = modeJouissanceConge;
        }

        public Double getHeuresParSemaine() {
            return heuresParSemaine;
        }

        public void setHeuresParSemaine(Double heuresParSemaine) {
            this.heuresParSemaine = heuresParSemaine;
        }

        public Double getHeuresParJour() {
            return heuresParJour;
        }

        public void setHeuresParJour(Double heuresParJour) {
            this.heuresParJour = heuresParJour;
        }

        public Double getNbrJourConge() {
            return nbrJourConge;
        }

        public void setNbrJourConge(Double nbrJourConge) {
            this.nbrJourConge = nbrJourConge;
        }

        public Double getNbrJourTravail() {
            return nbrJourTravail;
        }

        public void setNbrJourTravail(Double nbrJourTravail) {
            this.nbrJourTravail = nbrJourTravail;
        }

        public void setVps(Double vps) {
            this.vps = vps;
        }

        public Boolean getEstEntreprisePrincipale() {
            return estEntreprisePrincipale;
        }

        public void setEstEntreprisePrincipale(Boolean estEntreprisePrincipale) {
            this.estEntreprisePrincipale = estEntreprisePrincipale;
        }

        public String getNss() {
            return nss;
        }

        public void setNss(String nss) {
            this.nss = nss;
        }

        public BigDecimal getRss() {
            return rss;
        }

        public void setRss(BigDecimal rss) {
            this.rss = rss;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getWebSite() {
            return webSite;
        }

        public void setWebSite(String webSite) {
            this.webSite = webSite;
        }

        public String getRccm() {
            return rccm;
        }

        public void setRccm(String rccm) {
            this.rccm = rccm;
        }

        public String getIfu() {
            return ifu;
        }

        public void setIfu(String ifu) {
            this.ifu = ifu;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDirectorName() {
            return directorName;
        }

        public void setDirectorName(String directorName) {
            this.directorName = directorName;
        }

        public String getDirectorEmail() {
            return directorEmail;
        }

        public void setDirectorEmail(String directorEmail) {
            this.directorEmail = directorEmail;
        }

        public String getDirectorPhone() {
            return directorPhone;
        }

        public void setDirectorPhone(String directorPhone) {
            this.directorPhone = directorPhone;
        }

        public Short getStatusId() {
            return statusId;
        }

        public void setStatusId(Short statusId) {
            this.statusId = statusId;
        }

        public BigDecimal getTvaVal() {
            return tvaVal;
        }

        public void setTvaVal(BigDecimal tvaVal) {
            this.tvaVal = tvaVal;
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

        public LocalDateTime getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
        }

        public Long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }

        public Long getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(Long updatedBy) {
            this.updatedBy = updatedBy;
        }

        public BigDecimal getCa() {
            return ca;
        }

        public void setCa(BigDecimal ca) {
            this.ca = ca;
        }
        //    public Set<ActivityArea> getActivityAreas() {
    //        return activityAreas;
    //    }
    //
    //    public void setActivityAreas(Set<ActivityArea> activityAreas) {
    //        this.activityAreas = activityAreas;
    //    }
    }
