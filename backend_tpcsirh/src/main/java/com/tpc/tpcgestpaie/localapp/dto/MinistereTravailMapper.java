package com.tpc.tpcgestpaie.localapp.dto;

import com.tpc.tpcgestpaie.localapp.model.MinistereTravail;

public class MinistereTravailMapper {

    public static MinistereTravailDTO toDTO(MinistereTravail entity) {
        MinistereTravailDTO dto = new MinistereTravailDTO();
        dto.setId(entity.getId());
        dto.setMinistere(entity.getMinistere());
        dto.setServiceSecuriteSociale(entity.getServiceSecuriteSociale());
        dto.setDirectionEnregistrementContrat(entity.getDirectionEnregistrementContrat());
        dto.setDirecteurDepartementalTravail(entity.getDirecteurDepartementalTravail());
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());

        if (entity.getAdded_by() != null) {
            dto.setAddedBy(entity.getAdded_by().getUsername()); // suppose que User a un champ username
        }
        return dto;
    }
}
