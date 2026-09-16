package com.tpc.tpcgestpaie.localapp.service.contrat;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SnapshotService {

    private final ObjectMapper objectMapper;

    public SnapshotService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Désérialiser le snapshot en objet ContratEmploye
    public ContratEmploye getAncienContratFromSnapshot(String snapshotJson) throws Exception {
        return objectMapper.readValue(snapshotJson, ContratEmploye.class);
    }

    // Comparer deux états
    public Map<String, Difference> comparerEtats(ContratEmploye ancien, ContratEmploye nouveau) {
        Map<String, Difference> differences = new HashMap<>();

        if (!Objects.equals(ancien.getType_contrat(), nouveau.getType_contrat())) {
            differences.put("typeContrat", new Difference(
                    ancien.getType_contrat(),
                    nouveau.getType_contrat()
            ));
        }

        if (!Objects.equals(ancien.getSalaire_base(), nouveau.getSalaire_base())) {
            differences.put("salaireBase", new Difference(
                    ancien.getSalaire_base(),
                    nouveau.getSalaire_base()
            ));
        }

        // Comparer les IDs des relations
        if (!Objects.equals(
                ancien.getDepartement() != null ? ancien.getDepartement().getId() : null,
                nouveau.getDepartement() != null ? nouveau.getDepartement().getId() : null
        )) {
            differences.put("departement", new Difference(
                    ancien.getDepartement() != null ? ancien.getDepartement().getLibelle() : null,
                    nouveau.getDepartement() != null ? nouveau.getDepartement().getLibelle() : null
            ));
        }

        return differences;
    }

    public static class Difference {
        private Object ancienneValeur;
        private Object nouvelleValeur;

        public Difference(Object ancienneValeur, Object nouvelleValeur) {
            this.ancienneValeur = ancienneValeur;
            this.nouvelleValeur = nouvelleValeur;
        }

        // Getters
        public Object getAncienneValeur() { return ancienneValeur; }
        public Object getNouvelleValeur() { return nouvelleValeur; }
    }
}