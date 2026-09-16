    package com.tpc.tpcgestpaie.localapp.dto.bulletin;

    import java.util.Map;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import java.util.Map;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class BulletinPaieGenerateDTO {

        private Long employeId;
        private String nomPrenomEmploye;
        private String matriculeEmploye;
        private Long contratEmployeId;
        private String numeroCompte;
        private String numeroCompteEmploye;
        private Long banqueId;
        private Long companyId;
        private String mois;
        private String dateCalculSalaire;
        private String statut;
        private String signataire;
        private Integer nombreEnfant;
        private Double soldeConge;
        private Double congePris;
        private Double tempsTravail;
        private Double heuresSup;
        private Double salaireBase;
        private Map<String, Double> primes;
        private Map<String, Double>   detailsSalaire;
        private Double primesExceptionnelles;
        private Double primes13eMois;

        private Double primeAnciennete;
        private Double autreAvantage;
        private Double salaireButPrecedant;
        private Double salaireBrut;
        private Double salaireBrutArrondi;
        private Double montantCnss;
        private Double montantIpts;
        private Double totalChargeSalarialRetenue;
        private Double totalRetenueNet;
        private Double totalRetenue;
        private Double mensualite;
        private Double avance;
        private Double acompte;
        private Double montantCnssEmployeur;
        private Double montantVps;
        private Double montantAib;
        private Double totalChargePatronale;
        private Double salaireNet;
        private Double netAPayer;
        private Double totalAutreRetenue;

        private Double taxeRadiophonique;
        private Double taxeTelevisuel;

        private String natureContrat;
        private String typeContrat;
        private String fonction;
        private String numeroCnss;
        private String numeroCnssEmployeur;
        private String dateDebut;
        private String dateFin;
        private String modePaiement;
        private String qrCodeImg;


        // ➕ Nouveaux champs pour front
        private String nomBanque;
        private String nomEntreprise;
        private String logoEntreprise;

        private String adresse;
        private String telephone;
        private String rccm;
        private String mail;
        private String site;


        private String qrCodeData;
    // URL ou base64 selon ce que tu fournis
        // ✅ Getters et Setters pour tous les champs
        // (à générer automatiquement avec ton IDE)


        public Map<String, Double> getDetailsSalaire() {
            return detailsSalaire;
        }

        public void setDetailsSalaire(Map<String, Double> detailsSalaire) {
            this.detailsSalaire = detailsSalaire;
        }

        public Double getTaxeTelevisuel() {
            return taxeTelevisuel;
        }

        public void setTaxeTelevisuel(Double taxeTelevisuel) {
            this.taxeTelevisuel = taxeTelevisuel;
        }

        public Double getTaxeRadiophonique() {
            return taxeRadiophonique;
        }

        public void setTaxeRadiophonique(Double taxeRadiophonique) {
            this.taxeRadiophonique = taxeRadiophonique;
        }

        public String getQrCodeImg() {
            return qrCodeImg;
        }

        public void setQrCodeImg(String qrCodeImg) {
            this.qrCodeImg = qrCodeImg;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getRccm() {
            return rccm;
        }

        public void setRccm(String rccm) {
            this.rccm = rccm;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getNatureContrat() {
            return natureContrat;
        }

        public void setNatureContrat(String natureContrat) {
            this.natureContrat = natureContrat;
        }

        public String getTypeContrat() {
            return typeContrat;
        }

        public void setTypeContrat(String typeContrat) {
            this.typeContrat = typeContrat;
        }

        public String getFonction() {
            return fonction;
        }

        public void setFonction(String fonction) {
            this.fonction = fonction;
        }

        public String getNumeroCnss() {
            return numeroCnss;
        }

        public void setNumeroCnss(String numeroCnss) {
            this.numeroCnss = numeroCnss;
        }

        public String getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(String dateDebut) {
            this.dateDebut = dateDebut;
        }

        public String getDateFin() {
            return dateFin;
        }

        public void setDateFin(String dateFin) {
            this.dateFin = dateFin;
        }

        public String getModePaiement() {
            return modePaiement;
        }

        public void setModePaiement(String modePaiement) {
            this.modePaiement = modePaiement;
        }

        public String getQrCodeData() {
            return qrCodeData;
        }

        public void setQrCodeData(String qrCodeData) {
            this.qrCodeData = qrCodeData;
        }

        public Long getEmployeId() {
            return employeId;
        }

        public void setEmployeId(Long employeId) {
            this.employeId = employeId;
        }

        public String getNomPrenomEmploye() {
            return nomPrenomEmploye;
        }

        public void setNomPrenomEmploye(String nomPrenomEmploye) {
            this.nomPrenomEmploye = nomPrenomEmploye;
        }

        public String getMatriculeEmploye() {
            return matriculeEmploye;
        }

        public void setMatriculeEmploye(String matriculeEmploye) {
            this.matriculeEmploye = matriculeEmploye;
        }

        public Long getContratEmployeId() {
            return contratEmployeId;
        }

        public void setContratEmployeId(Long contratEmployeId) {
            this.contratEmployeId = contratEmployeId;
        }

        public String getNumeroCompte() {
            return numeroCompte;
        }

        public void setNumeroCompte(String numeroCompte) {
            this.numeroCompte = numeroCompte;
        }

        public String getNumeroCompteEmploye() {
            return numeroCompteEmploye;
        }

        public void setNumeroCompteEmploye(String numeroCompteEmploye) {
            this.numeroCompteEmploye = numeroCompteEmploye;
        }

        public Long getBanqueId() {
            return banqueId;
        }

        public void setBanqueId(Long banqueId) {
            this.banqueId = banqueId;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }

        public String getMois() {
            return mois;
        }

        public void setMois(String mois) {
            this.mois = mois;
        }

        public String getDateCalculSalaire() {
            return dateCalculSalaire;
        }

        public void setDateCalculSalaire(String dateCalculSalaire) {
            this.dateCalculSalaire = dateCalculSalaire;
        }

        public String getStatut() {
            return statut;
        }

        public void setStatut(String statut) {
            this.statut = statut;
        }

        public String getSignataire() {
            return signataire;
        }

        public void setSignataire(String signataire) {
            this.signataire = signataire;
        }

        public Integer getNombreEnfant() {
            return nombreEnfant;
        }

        public void setNombreEnfant(Integer nombreEnfant) {
            this.nombreEnfant = nombreEnfant;
        }

        public Double getSoldeConge() {
            return soldeConge;
        }

        public void setSoldeConge(Double soldeConge) {
            this.soldeConge = soldeConge;
        }

        public Double getCongePris() {
            return congePris;
        }

        public void setCongePris(Double congePris) {
            this.congePris = congePris;
        }

        public Double getTempsTravail() {
            return tempsTravail;
        }

        public void setTempsTravail(Double tempsTravail) {
            this.tempsTravail = tempsTravail;
        }

        public Double getHeuresSup() {
            return heuresSup;
        }

        public void setHeuresSup(Double heuresSup) {
            this.heuresSup = heuresSup;
        }

        public Double getSalaireBase() {
            return salaireBase;
        }

        public void setSalaireBase(Double salaireBase) {
            this.salaireBase = salaireBase;
        }

        public Map<String, Double> getPrimes() {
            return primes;
        }

        public void setPrimes(Map<String, Double> primes) {
            this.primes = primes;
        }

        public Double getPrimesExceptionnelles() {
            return primesExceptionnelles;
        }

        public void setPrimesExceptionnelles(Double primesExceptionnelles) {
            this.primesExceptionnelles = primesExceptionnelles;
        }

        public Double getPrimes13eMois() {
            return primes13eMois;
        }

        public void setPrimes13eMois(Double primes13eMois) {
            this.primes13eMois = primes13eMois;
        }

        public Double getAutreAvantage() {
            return autreAvantage;
        }

        public void setAutreAvantage(Double autreAvantage) {
            this.autreAvantage = autreAvantage;
        }

        public Double getSalaireButPrecedant() {
            return salaireButPrecedant;
        }

        public void setSalaireButPrecedant(Double salaireButPrecedant) {
            this.salaireButPrecedant = salaireButPrecedant;
        }

        public Double getSalaireBrut() {
            return salaireBrut;
        }

        public void setSalaireBrut(Double salaireBrut) {
            this.salaireBrut = salaireBrut;
        }

        public Double getSalaireBrutArrondi() {
            return salaireBrutArrondi;
        }

        public void setSalaireBrutArrondi(Double salaireBrutArrondi) {
            this.salaireBrutArrondi = salaireBrutArrondi;
        }

        public Double getMontantCnss() {
            return montantCnss;
        }

        public void setMontantCnss(Double montantCnss) {
            this.montantCnss = montantCnss;
        }

        public Double getMontantIpts() {
            return montantIpts;
        }

        public void setMontantIpts(Double montantIpts) {
            this.montantIpts = montantIpts;
        }

        public Double getTotalChargeSalarialRetenue() {
            return totalChargeSalarialRetenue;
        }

        public void setTotalChargeSalarialRetenue(Double totalChargeSalarialRetenue) {
            this.totalChargeSalarialRetenue = totalChargeSalarialRetenue;
        }

        public Double getTotalRetenueNet() {
            return totalRetenueNet;
        }

        public void setTotalRetenueNet(Double totalRetenueNet) {
            this.totalRetenueNet = totalRetenueNet;
        }

        public Double getTotalRetenue() {
            return totalRetenue;
        }

        public void setTotalRetenue(Double totalRetenue) {
            this.totalRetenue = totalRetenue;
        }

        public Double getMensualite() {
            return mensualite;
        }

        public void setMensualite(Double mensualite) {
            this.mensualite = mensualite;
        }

        public Double getAvance() {
            return avance;
        }

        public void setAvance(Double avance) {
            this.avance = avance;
        }

        public Double getAcompte() {
            return acompte;
        }

        public void setAcompte(Double acompte) {
            this.acompte = acompte;
        }

        public Double getMontantCnssEmployeur() {
            return montantCnssEmployeur;
        }

        public void setMontantCnssEmployeur(Double montantCnssEmployeur) {
            this.montantCnssEmployeur = montantCnssEmployeur;
        }

        public Double getMontantVps() {
            return montantVps;
        }

        public void setMontantVps(Double montantVps) {
            this.montantVps = montantVps;
        }

        public Double getMontantAib() {
            return montantAib;
        }

        public void setMontantAib(Double montantAib) {
            this.montantAib = montantAib;
        }

        public Double getTotalChargePatronale() {
            return totalChargePatronale;
        }

        public void setTotalChargePatronale(Double totalChargePatronale) {
            this.totalChargePatronale = totalChargePatronale;
        }

        public String getNumeroCnssEmployeur() {
            return numeroCnssEmployeur;
        }

        public void setNumeroCnssEmployeur(String numeroCnssEmployeur) {
            this.numeroCnssEmployeur = numeroCnssEmployeur;
        }

        public Double getSalaireNet() {
            return salaireNet;
        }

        public void setSalaireNet(Double salaireNet) {
            this.salaireNet = salaireNet;
        }

        public Double getNetAPayer() {
            return netAPayer;
        }

        public void setNetAPayer(Double netAPayer) {
            this.netAPayer = netAPayer;
        }

        public Double getTotalAutreRetenue() {
            return totalAutreRetenue;
        }

        public void setTotalAutreRetenue(Double totalAutreRetenue) {
            this.totalAutreRetenue = totalAutreRetenue;
        }

        public String getNomBanque() {
            return nomBanque;
        }

        public void setNomBanque(String nomBanque) {
            this.nomBanque = nomBanque;
        }

        public String getNomEntreprise() {
            return nomEntreprise;
        }

        public void setNomEntreprise(String nomEntreprise) {
            this.nomEntreprise = nomEntreprise;
        }

        public String getLogoEntreprise() {
            return logoEntreprise;
        }

        public void setLogoEntreprise(String logoEntreprise) {
            this.logoEntreprise = logoEntreprise;
        }

        public Double getPrimeAnciennete() {
            return primeAnciennete;
        }

        public void setPrimeAnciennete(Double primeAnciennete) {
            this.primeAnciennete = primeAnciennete;
        }
    }
