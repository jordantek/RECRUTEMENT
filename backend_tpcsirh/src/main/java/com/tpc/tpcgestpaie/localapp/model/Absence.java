    package com.tpc.tpcgestpaie.localapp.model;

    import jakarta.persistence.*;

    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Entity
    // Absences
    @Table(name = "absences")
    public class Absence  {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "employe_id")
        private Employe employe;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "contrat_employe_id")
        private ContratEmploye contratEmploye;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "company_id")
        private Company company;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "motif_absence_id")
        private MotifAbsence motifAbsence;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "type_absence_id")
        private TypeAbsence typeAbsence;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "credit_conge_id")
        private CreditConge creditConge;

        private String libelle;
        private  String modeJouissance;
        private  String conditionAcceptation;

        @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        private boolean deductibleTempsTravail;

        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String duree;

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

        public Employe getEmploye() {
            return employe;
        }

        public void setEmploye(Employe employe) {
            this.employe = employe;
        }

        public ContratEmploye getContratEmploye() {
            return contratEmploye;
        }

        public void setContratEmploye(ContratEmploye contratEmploye) {
            this.contratEmploye = contratEmploye;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public MotifAbsence getMotifAbsence() {
            return motifAbsence;
        }

        public void setMotifAbsence(MotifAbsence motifAbsence) {
            this.motifAbsence = motifAbsence;
        }

        public TypeAbsence getTypeAbsence() {
            return typeAbsence;
        }

        public void setTypeAbsence(TypeAbsence typeAbsence) {
            this.typeAbsence = typeAbsence;
        }

        public CreditConge getCreditConge() {
            return creditConge;
        }

        public void setCreditConge(CreditConge creditConge) {
            this.creditConge = creditConge;
        }

        public String getLibelle() {
            return libelle;
        }

        public void setLibelle(String libelle) {
            this.libelle = libelle;
        }

        public String getModeJouissance() {
            return modeJouissance;
        }

        public void setModeJouissance(String modeJouissance) {
            this.modeJouissance = modeJouissance;
        }

        public String getConditionAcceptation() {
            return conditionAcceptation;
        }

        public void setConditionAcceptation(String conditionAcceptation) {
            this.conditionAcceptation = conditionAcceptation;
        }

        public boolean isDeductibleTempsTravail() {
            return deductibleTempsTravail;
        }

        public void setDeductibleTempsTravail(boolean deductibleTempsTravail) {
            this.deductibleTempsTravail = deductibleTempsTravail;
        }

        public LocalDate getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDate getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }

        public String getDuree() {
            return duree;
        }

        public void setDuree(String duree) {
            this.duree = duree;
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