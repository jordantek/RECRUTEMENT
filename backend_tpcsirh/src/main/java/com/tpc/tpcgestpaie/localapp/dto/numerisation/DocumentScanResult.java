package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentScanResult {
    private boolean valid;
    private String documentId;
    private String digitalId;
    private Long employeId;
    private String employeNom;
    private String employeMatricule;
    private String employePrenom;
    private String documentType;
    private String originalFileName;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private String securityLevel;
    private Integer remainingScans;
    private String validationMessage;
}
