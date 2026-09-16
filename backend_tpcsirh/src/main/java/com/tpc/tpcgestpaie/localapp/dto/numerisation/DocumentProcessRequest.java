package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentProcessRequest {
    // Champs obligatoires existants
    private Long employeId;
    private Long subCategoryId;
    private Boolean replaceSignatures = true;
    private Boolean generateQR = true;

    // 🔥 NOUVEAUX CHAMPS ADDITIONNELS
    private String reference;           // Référence du document (ex: DOC-2024-001)
    private String signedBy;            // Email/Nom de la personne qui upload
    private LocalDate expirationDate;   // Date d'expiration (optionnelle)
    private Boolean isVerified;         // Document déjà vérifié?
    private String notes;               // Notes additionnelles (optionnel)

    // Getters et Setters
    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Boolean getReplaceSignatures() {
        return replaceSignatures;
    }

    public void setReplaceSignatures(Boolean replaceSignatures) {
        this.replaceSignatures = replaceSignatures;
    }

    public Boolean getGenerateQR() {
        return generateQR;
    }

    public void setGenerateQR(Boolean generateQR) {
        this.generateQR = generateQR;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}