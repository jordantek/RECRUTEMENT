package com.tpc.tpcgestpaie.localapp.dto.contrat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;

public class StatutContratMapper {

    public static StatutContratDTO toDTO(StatutContrat entity) {
        if (entity == null) return null;

        StatutContratDTO dto = new StatutContratDTO();
        dto.setId(entity.getId());
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setCategorieEmployeId(entity.getCategorieEmploye() != null ? entity.getCategorieEmploye().getId() : null);
        dto.setTypeContrat(entity.getTypeContrat());
        dto.setDepartementId(entity.getDepartement() != null ? entity.getDepartement().getId() : null);
        dto.setPosteId(entity.getPoste() != null ? entity.getPoste().getId() : null);
        dto.setDateFinContrat(entity.getDateFinContrat());
        dto.setSalaireBase(entity.getSalaireBase());
        dto.setSalaireBrut(entity.getSalaireBrut());
        dto.setTypeModification(entity.getTypeModification() != null ? entity.getTypeModification().name() : null);
        dto.setMotif(entity.getMotif());
        dto.setPreuve(entity.getPreuve());
        dto.setActif(entity.isActif());
        dto.setInitialisation(entity.isInitialisation());
        dto.setDateEffet(entity.getDateEffet());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // 🔥 Snapshot brut
        dto.setSnapshotJson(entity.getSnapshotJson());

// 🔥 Snapshot désérialisé
        if (entity.getSnapshotJson() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                dto.setSnapshotJson(entity.getSnapshotJson());
            } catch (Exception e) {
                dto.setSnapshotJson(null); // en cas d'erreur de parsing
            }
        }
        return dto;
    }

    public static StatutContrat toEntity(StatutContratDTO dto) {
        if (dto == null) return null;

        StatutContrat entity = new StatutContrat();
        entity.setId(dto.getId());
        // Relations seront gérées par le service (ContratEmploye, Categorie, Poste, Departement)
        entity.setTypeContrat(dto.getTypeContrat());
        entity.setDateFinContrat(dto.getDateFinContrat());
        entity.setSalaireBase(dto.getSalaireBase());
        entity.setSalaireBrut(dto.getSalaireBrut());
        if (dto.getTypeModification() != null) {
            entity.setTypeModification(StatutContrat.TypeModification.valueOf(dto.getTypeModification()));
        }
        entity.setMotif(dto.getMotif());
        entity.setPreuve(dto.getPreuve());
        entity.setActif(dto.isActif());
        entity.setInitialisation(dto.isInitialisation());
        entity.setDateEffet(dto.getDateEffet());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        // 🔥 Snapshot JSON
        entity.setSnapshotJson(dto.getSnapshotJson());

        return entity;
    }
}
