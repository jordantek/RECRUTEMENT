package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnfantEmployeHelper {

    public List<ErrorResponse> getInvalidFieldMessages(EnfantEmploye e) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (e.getNom() == null || e.getNom().trim().isEmpty()) {
            errors.add(new ErrorResponse("nom", "Le nom est obligatoire."));
        }

        if (e.getPrenom() == null || e.getPrenom().trim().isEmpty()) {
            errors.add(new ErrorResponse("prenom", "Le prénom est obligatoire."));
        }

        if (e.getSexe() == null) {
            errors.add(new ErrorResponse("sexe", "Le sexe est obligatoire."));
        }

        if (e.getDateNaissance() == null) {
            errors.add(new ErrorResponse("dateNaissance", "La date de naissance est obligatoire."));
        }

        if (e.getLieuNaissance() == null || e.getLieuNaissance().trim().isEmpty()) {
            errors.add(new ErrorResponse("lieuNaissance", "Le lieu de naissance est obligatoire."));
        }

        // Si tu veux, tu peux aussi vérifier que l'enfant n'est pas décédé (ou autre logique métier)
        if (e.getEstDecede() == null) {
            errors.add(new ErrorResponse("estDecede", "Le statut de décès doit être renseigné."));
        }

        return errors;
    }
}