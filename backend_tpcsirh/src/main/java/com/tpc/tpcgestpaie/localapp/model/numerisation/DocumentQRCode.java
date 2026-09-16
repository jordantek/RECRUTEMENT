package com.tpc.tpcgestpaie.localapp.model.numerisation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

// DocumentQRCode.java (si pas déjà existant)
@Entity
@Table(name = "document_qr_codes")
@Data
public class DocumentQRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private EmployeeDocument document;

    @Column(columnDefinition = "TEXT")
    private String qrData; // Données JSON du QR Code

    @Column(columnDefinition = "TEXT")
    private String qrImageBase64; // QR Code en base64

    private String digitalId;
    private LocalDateTime generatedAt;

    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature; // Signature RSA des données

    @Column(name = "public_key_id", length = 50)
    private String publicKeyId = "COMPANY_KEY_2024"; // ID de la clé

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    // Modifiez le PrePersist :
    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
        this.digitalId = "DIGI_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        this.expiresAt = LocalDateTime.now().plusYears(1); // Expire dans 1 an
        if (this.isRevoked == null) this.isRevoked = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeDocument getDocument() {
        return document;
    }

    public void setDocument(EmployeeDocument document) {
        this.document = document;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getQrImageBase64() {
        return qrImageBase64;
    }

    public void setQrImageBase64(String qrImageBase64) {
        this.qrImageBase64 = qrImageBase64;
    }

    public String getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(String digitalId) {
        this.digitalId = digitalId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}