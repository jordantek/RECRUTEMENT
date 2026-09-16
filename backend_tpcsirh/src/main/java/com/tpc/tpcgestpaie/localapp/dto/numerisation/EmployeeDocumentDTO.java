package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class EmployeeDocumentDTO {
    private Long id;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private LocalDate expirationDate;
    private Boolean verified;
    private String documentType;
    private String subCategoryName;
    private String qrCodeUrl;
    private String downloadUrl;

    private String signedBy;
    private String reference;

    // Informations employé
    private Long employeId;
    private String employeNomComplet;
    private String employeMatricule;

    public static EmployeeDocumentDTO fromEntity(EmployeeDocument document) {
        EmployeeDocumentDTO dto = new EmployeeDocumentDTO();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setMimeType(document.getMimeType());
        dto.setFileSize(document.getFileSize());
        dto.setUploadDate(document.getCreatedAt());
        dto.setExpirationDate(document.getExpirationDate());
        dto.setVerified(document.getIsVerified());
        dto.setDocumentType(document.getDocumentType().name());
        dto.setSignedBy(document.getSignedBy());
        dto.setReference(document.getReference());

        if (document.getSubCategory() != null) {
            dto.setSubCategoryName(document.getSubCategory().getName());
        }

        if (document.getQrCode() != null) {
//            dto.setQrCodeUrl("/api/document-qrcodes/verify/" + document.getQrCode().getUniqueCode());
        }

        // 🔥 URL de téléchargement du document reconstruit
        dto.setDownloadUrl("/api/employee-documents/" + document.getId() + "/download");

        // Informations employé
        if (document.getEmploye() != null) {
            dto.setEmployeId(document.getEmploye().getId());
            dto.setEmployeNomComplet(document.getEmploye().getPrenom() + " " + document.getEmploye().getNom());
            dto.setEmployeMatricule(document.getEmploye().getMatricule());
        }

        return dto;
    }

    public static List<EmployeeDocumentDTO> fromEntities(List<EmployeeDocument> documents) {
        return documents.stream()
                .map(EmployeeDocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }
}