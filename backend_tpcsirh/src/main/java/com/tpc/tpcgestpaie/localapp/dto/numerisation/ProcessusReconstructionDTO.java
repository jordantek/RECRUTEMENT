package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentReconstructionProcess;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ProcessusReconstructionDTO {
    private Long id;
    private Long originalDocumentId;
    private Long reconstructedDocumentId;
    private String status;
    private String processType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> metadataScan;

    // Informations supplémentaires
    private String originalDocumentName;
    private String employeeName;
    private String employeeMatricule;

    public static ProcessusReconstructionDTO fromEntity(DocumentReconstructionProcess processus) {
        ProcessusReconstructionDTO dto = new ProcessusReconstructionDTO();
        dto.setId(processus.getId());

        if (processus.getOriginalDocument() != null) {
            dto.setOriginalDocumentId(processus.getOriginalDocument().getId());
            dto.setOriginalDocumentName(processus.getOriginalDocument().getFileName());

            if (processus.getOriginalDocument().getEmploye() != null) {
                dto.setEmployeeName(processus.getOriginalDocument().getEmploye().getPrenom() + " " +
                        processus.getOriginalDocument().getEmploye().getNom());
                dto.setEmployeeMatricule(processus.getOriginalDocument().getEmploye().getMatricule());
            }
        }

        if (processus.getReconstructedDocument() != null) {
            dto.setReconstructedDocumentId(processus.getReconstructedDocument().getId());
        }

        dto.setStatus(processus.getStatus().name());
        dto.setProcessType(processus.getProcessType() != null ? processus.getProcessType().name() : null);
        dto.setStartDate(processus.getStartDate());
        dto.setEndDate(processus.getEndDate());
//        dto.setMetadataScan(processus.getProcessMetadata());

        return dto;
    }

    public static List<ProcessusReconstructionDTO> fromEntities(List<DocumentReconstructionProcess> processus) {
        return processus.stream()
                .map(ProcessusReconstructionDTO::fromEntity)
                .collect(Collectors.toList());
    }
}