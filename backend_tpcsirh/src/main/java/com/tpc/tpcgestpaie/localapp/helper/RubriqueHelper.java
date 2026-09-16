package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.repository.ColonneAffichageRepository;
import com.tpc.tpcgestpaie.localapp.repository.NiveauAffichageRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RubriqueHelper {

    private final RubriqueRepository rubriqueRepository;
    private final NiveauAffichageRepository niveauAffichageRepository;
    private final ColonneAffichageRepository colonneAffichageRepository;
    private final UserRepository userRepository;

    public RubriqueHelper(RubriqueRepository rubriqueRepository,
                          NiveauAffichageRepository niveauAffichageRepository,
                          ColonneAffichageRepository colonneAffichageRepository, UserRepository userRepository) {
        this.rubriqueRepository = rubriqueRepository;
        this.niveauAffichageRepository = niveauAffichageRepository;
        this.colonneAffichageRepository = colonneAffichageRepository;
        this.userRepository = userRepository;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean isRubriqueValide(Rubrique r) {
        return getInvalidFieldMessages(r).isEmpty();
    }

    public List<ErrorResponse> getInvalidFieldMessages(Rubrique r) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (isBlank(r.getLibelle())) {
            errors.add(new ErrorResponse("libelle", "Le libellé est obligatoire"));
        } else if (rubriqueRepository.existsByLibelle(r.getLibelle())) {
            errors.add(new ErrorResponse("libelle_existe", "Ce libellé existe déjà"));
        }

        if (isBlank(r.getNature())) {
            errors.add(new ErrorResponse("nature", "La nature est obligatoire"));
        } else if (isNatureInvalid(r)) {
            errors.add(new ErrorResponse("nature_invalide", "La nature est invalide. Valeurs autorisées :  AVANTAGE, RETENUE"));
        }

        if (r.getNiveauAffichage() == null) {
            errors.add(new ErrorResponse("niveau_affichage_id", "Le niveau d'affichage est obligatoire"));
        } else if (!niveauAffichageRepository.existsById(r.getNiveauAffichage().getId())) {
            errors.add(new ErrorResponse("niveau_affichage_inexistant", "Le niveau d'affichage est inexistant"));
        }

        if (r.getColonneAffichage() == null) {
            errors.add(new ErrorResponse("colonne_affichage_id", "La colonne d'affichage est obligatoire"));
        } else if (!colonneAffichageRepository.existsById(r.getColonneAffichage().getId())) {
            errors.add(new ErrorResponse("colonne_affichage_inexistant", "La colonne d'affichage est inexistante"));
        }

        if (!GlobalEnums.BoolValid.BoolValid(r.getRubriqueImposable())) {
            errors.add(new ErrorResponse(
                    "est_imposable",
                    "Choisissez entre OUI et NON si la rubrique est imposable ou pas"
            ));
        }

       return errors;
    }

    public boolean isNatureInvalid(Rubrique rubrique) {
        try {
            GlobalEnums.NatureECS.valueOf(rubrique.getNature().toUpperCase());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

}
