package com.tpc.tpcgestpaie.localapp.dto.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratCdiDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import org.springframework.stereotype.Component;

@Component
public class ContratCdiMapper {

    public ContratCdiDTO toDto(ContratEmploye contrat) {
        if (contrat == null) {
            return null;
        }

        ContratCdiDTO dto = new ContratCdiDTO();

        return dto;
    }
}
