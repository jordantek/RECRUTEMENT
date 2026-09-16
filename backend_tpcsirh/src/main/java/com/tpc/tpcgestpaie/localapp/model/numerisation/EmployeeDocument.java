    package com.tpc.tpcgestpaie.localapp.model.numerisation;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.tpc.tpcgestpaie.localapp.model.Employe;
    import jakarta.persistence.*;
    import lombok.Data;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    @Entity
    @Table(name = "employee_documents")
    @Data
    public class EmployeeDocument {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "employe_id", nullable = false)
        @JsonIgnore
        private Employe employe;

        @Column(name = "file_hash", length = 64)
        private String fileHash;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sub_category_id", nullable = false)
        private DocumentSubCategory subCategory;

        @Column(nullable = false, length = 500)
        private String fileName;

        @Column(nullable = false, length = 1000)
        private String filePath;

        private String mimeType;

        private String reference;
        private Long fileSize;

        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        private LocalDate expirationDate;
        private Boolean isVerified = false;

        @Enumerated(EnumType.STRING)
        private DocumentType documentType = DocumentType.ORIGINAL;

        @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private DocumentQRCode qrCode;

        @Column(name = "content_hash", length = 64)
        private String contentHash; // SHA256 du contenu du fichier

        @Column(name = "signed_by", length = 100)
        private String signedBy; // Email de l'admin qui a uploadé

        @Column(name = "signature_timestamp")
        private LocalDateTime signatureTimestamp;

        @Column(name = "is_active")
        private Boolean isActive = true; // Pour désactiver si faux

        @Column(name = "verification_count")
        private Integer verificationCount = 0;

        // Modifiez le PrePersist :
        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
            this.signatureTimestamp = LocalDateTime.now();
            if (this.isActive == null) this.isActive = true;
            if (this.verificationCount == null) this.verificationCount = 0;
        }
        public enum DocumentType { ORIGINAL, RECONSTRUCTED, GENERATED }
    }

