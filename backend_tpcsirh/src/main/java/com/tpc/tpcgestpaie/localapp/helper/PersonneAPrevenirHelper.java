package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonneAPrevenirHelper {

    public List<ErrorResponse> getInvalidFieldMessages(PersonneAPrevenir p) {
        List<ErrorResponse> errors = new ArrayList<>();

//        if (p == null) {
//            errors.add(new ErrorResponse("personneAPrevenir", "L'objet PersonneAPrevenir ne peut pas être null."));
//            return errors;
//        }

//        if (p.getEmploye() == null) {
//            errors.add(new ErrorResponse("employe", "L'employé associé est obligatoire."));
//        }

        if (p.getNomPrenom() == null || p.getNomPrenom().trim().isEmpty()) {
            errors.add(new ErrorResponse("nomPrenom", "Le nom et prénom sont obligatoires."));
        }


        if (p.getLienParente() == null) {
            errors.add(new ErrorResponse("lienParente", "Le lien de parenté est obligatoire."));
        }

        if (p.getTelephone() == null || p.getTelephone().trim().isEmpty()) {
            errors.add(new ErrorResponse("telephone", "Le téléphone est obligatoire."));
        } else if (!p.getTelephone().matches("^[+\\d\\s-]{5,20}$")) {
            errors.add(new ErrorResponse("telephone", "Le format du téléphone est invalide."));
        }

        if (p.getAdresse() == null || p.getAdresse().trim().isEmpty()) {
            errors.add(new ErrorResponse("adresse", "L'adresse est obligatoire."));
        }

//        if (p.getEmail() != null && !p.getEmail().trim().isEmpty()) {
//            if (!p.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
//                errors.add(new ErrorResponse("email", "Le format de l'email est invalide."));
//            }
//        }

        // Tu peux ajouter d’autres validations métiers ici selon besoin

        return errors;
    }
}