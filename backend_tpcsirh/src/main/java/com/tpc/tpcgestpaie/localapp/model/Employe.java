    package com.tpc.tpcgestpaie.localapp.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonInclude;
    import jakarta.persistence.*;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.Period;
    import java.time.temporal.ChronoUnit;
    import java.util.HashSet;
    import java.util.Set;

    import com.fasterxml.jackson.annotation.JsonFormat;

    @Entity
    @Table(name = "employes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class Employe {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String matricule;

        private String titre;
        private String nom;
        private String prenom;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date_naissance;

        private String lieu_naissance;

        private String sexe;
        private String situationMatrimoniale;
        private String numeroIfu;
        private String telephone;
        private String email;
        private String nom_pere;
        private String nom_mere;
        private String boite_postale;
        private String maison;
        private String numeroCarre;
        private String quartier;
        private String nationalite;

        @Column(nullable = true, unique = true)
        private String numeroCnss;
        private String profession;
        private boolean is_employe_interne;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "company_id")
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        private Company company;

        @ManyToMany(mappedBy = "employes")
        @JsonIgnore
        private Set<Formation> formations = new HashSet<>();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "added_by")
        @JsonIgnore
        private User added_by;

        @Column(name = "created_at", updatable = false)
        private LocalDateTime created_at;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "updated_by")
        @JsonIgnore
        private User updated_by;

        private LocalDateTime updated_at;

        private LocalDateTime deleted_at;

        @PrePersist
        protected void onCreate() {
            created_at = LocalDateTime.now();
            updated_at = created_at;
        }


        public int getAge() {
            return Period.between(date_naissance, LocalDate.now()).getYears();
        }

        public LocalDate getProchainAnniversaire() {
            LocalDate now = LocalDate.now();
            LocalDate anniversaireThisYear = date_naissance.withYear(now.getYear());

            if (anniversaireThisYear.isBefore(now) || anniversaireThisYear.isEqual(now)) {
                return anniversaireThisYear.plusYears(1);
            }
            return anniversaireThisYear;
        }

        public long getJoursRestants() {
            return ChronoUnit.DAYS.between(LocalDate.now(), getProchainAnniversaire());
        }

        @PreUpdate
        protected void onUpdate() {
            updated_at = LocalDateTime.now();
        }

        public String getNumeroIfu() {
            return numeroIfu;
        }

        public void setNumeroIfu(String numeroIfu) {
            this.numeroIfu = numeroIfu;
        }

        public String getNumeroCnss() {
            return numeroCnss;
        }

        public Set<Formation> getFormations() {
            return formations;
        }

        public void setFormations(Set<Formation> formations) {
            this.formations = formations;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getSexe() {
            return sexe;
        }

        public void setSexe(String sexe) {
            this.sexe = sexe;
        }

        public String getSituationMatrimoniale() {
            return situationMatrimoniale;
        }

        public void setSituationMatrimoniale(String situationMatrimoniale) {
            this.situationMatrimoniale = situationMatrimoniale;
        }

        public LocalDate getDate_naissance() {
            return date_naissance;
        }

        public void setDate_naissance(LocalDate date_naissance) {
            this.date_naissance = date_naissance;
        }

        public String getLieu_naissance() {
            return lieu_naissance;
        }

        public void setLieu_naissance(String lieu_naissance) {
            this.lieu_naissance = lieu_naissance;
        }

        public String getNumero_ifu() {
            return numeroIfu;
        }

        public void setNumero_ifu(String numero_ifu) {
            this.numeroIfu = numero_ifu;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNom_pere() {
            return nom_pere;
        }

        public void setNom_pere(String nom_pere) {
            this.nom_pere = nom_pere;
        }

        public String getNom_mere() {
            return nom_mere;
        }

        public void setNom_mere(String nom_mere) {
            this.nom_mere = nom_mere;
        }

        public String getBoite_postale() {
            return boite_postale;
        }

        public void setBoite_postale(String boite_postale) {
            this.boite_postale = boite_postale;
        }

        public String getMaison() {
            return maison;
        }

        public void setMaison(String maison) {
            this.maison = maison;
        }

        public String getNumeroCarre() {
            return numeroCarre;
        }

        public void setNumeroCarre(String numeroCarre) {
            this.numeroCarre = numeroCarre;
        }

        public String getQuartier() {
            return quartier;
        }

        public void setQuartier(String quartier) {
            this.quartier = quartier;
        }

        public String getNationalite() {
            return nationalite;
        }

        public void setNationalite(String nationalite) {
            this.nationalite = nationalite;
        }

        public String getNumero_cnss() {
            return numeroCnss;
        }

        public void setNumero_cnss(String numero_cnss) {
            this.numeroCnss = numero_cnss;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public boolean isIs_employe_interne() {
            return is_employe_interne;
        }

        public void setIs_employe_interne(boolean is_employe_interne) {
            this.is_employe_interne = is_employe_interne;
        }

    //    public List<EmployeDiplome> getDiplomes() {
    //        return diplomes;
    //    }
    //
    //    public void setDiplomes(List<EmployeDiplome> diplomes) {
    //        this.diplomes = diplomes;
    //    }

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

        public User getUpdated_by() {
            return updated_by;
        }

        public void setUpdated_by(User updated_by) {
            this.updated_by = updated_by;
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

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }
    }
