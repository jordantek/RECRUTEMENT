package com.tpc.tpcgestpaie.localapp.model.numerisation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_reconstruction_process")
@Data
public class DocumentReconstructionProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_document_id")
    private EmployeeDocument originalDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconstructed_document_id")
    private EmployeeDocument reconstructedDocument;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status = ProcessStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // ⚠️ RETIRER TEMPORAIREMENT le champ JSON
    // @JdbcTypeCode(SqlTypes.JSON)
    // @Column(columnDefinition = "json")
    // private Map<String, Object> processMetadata = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        this.startDate = LocalDateTime.now();
    }

    // Enums définis à l'intérieur de la classe
    public enum ProcessStatus {
        PENDING, PROCESSING, COMPLETED, ERROR
    }

    public enum ProcessType {
        SCAN_RECONSTRUCTION, GENERATION
    }
}


