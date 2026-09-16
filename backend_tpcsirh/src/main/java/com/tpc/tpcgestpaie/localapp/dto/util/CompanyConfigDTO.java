package com.tpc.tpcgestpaie.localapp.dto.util;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompanyConfigDTO {

    private String name;
    private LocalDate creationDate;
    private String nss;
    private BigDecimal rss;

    private Double vps;
    private LocalDate vpsEffectDate;

    private String address;
    private String country;
    private String email;
    private String phone;
    private String webSite;
    private String rccm;
    private String ifu;
    private String logo;

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

    private BigDecimal tvaVal;

    // ✅ Getters & Setters
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

    public Double getVps() {
        return vps;
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

    public BigDecimal getTvaVal() {
        return tvaVal;
    }

    public void setTvaVal(BigDecimal tvaVal) {
        this.tvaVal = tvaVal;
    }
}
