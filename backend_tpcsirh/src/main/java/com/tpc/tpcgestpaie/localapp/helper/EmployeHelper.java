package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.stereotype.Component;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

@Component
public class EmployeHelper {

    // Vérifie si une chaîne est vide ou null
//    private boolean isBlank(String value) {
//        return !StringUtils.hasText(value);
//    }

    private final EmployeRepository employeRepository;

    public EmployeHelper(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    public boolean isMatriculeInvalid(String matricule) {
        return isBlank(matricule) ;
    }

    public boolean isMatriculeExist(String matricule) {
        return employeRepository.existsByMatricule(matricule);
    }

    public boolean isIfuExist(String ifu) {
        return employeRepository.existsByNumeroIfu(ifu);
    }


    public boolean isNumeroCnssExist(String numeroCnss) {
        return employeRepository.existsByNumeroCnss(numeroCnss);
    }


    public boolean isNomInvalid(String nom) {
        return isBlank(nom);
    }

    public boolean isPrenomInvalid(String prenom) {
        return isBlank(prenom);
    }

    public boolean isTitreInvalid(Employe employe) {
        try {
            GlobalEnums.Titres.valueOf(employe.getTitre().toUpperCase());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isSexeInvalid(Employe employe) {
        try {
            GlobalEnums.Sexe.valueOf(employe.getSexe().toUpperCase());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isSituationMatrimonialeInvalid(Employe employe) {
        try {
            GlobalEnums.MaritalSatus.valueOf(employe.getSituationMatrimoniale().toUpperCase());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isDateNaissanceInvalid(Employe employe) {
        LocalDate dateNaissance = employe.getDate_naissance();

        if (dateNaissance == null) {
            return true; // La date est nulle → invalide
        }

        if (dateNaissance.isAfter(LocalDate.now())) {
            return true; // La date est dans le futur → invalide
        }

        return false; // La date est valide
    }

    public boolean isLieuNaissanceInvalid(String lieu) {
        return isBlank(lieu);
    }


    public boolean isNumeroIfuInvalid(String ifu) {
        return isBlank(ifu);
    }

//    public boolean isTelephoneInvalid(String tel) {
//        System.out.println(tel);
//        return isBlank(tel) || !tel.matches("^[0-9]{8,15}$");
//    }

    public boolean isTelephoneInvalid(String tel) {
        if (isBlank(tel)) return true;

        String cleaned = tel.replaceAll("[\\s\\-]", "");

        // Accepte + ou 00 pour indicatif, suivi de 8 à 15 chiffres
        String regex = "^(\\+|00)?\\d{8,15}$";
        return !cleaned.matches(regex);
    }



    // VERIFICATION AVEC UNDESCORE
//    public boolean isTelephoneInvalid(String tel) {
//        if (isBlank(tel)) return true;
//
//        // Autoriser espace ou tiret (qu'on nettoie)
//        String cleaned = tel.replaceAll("[\\s\\-]", "");
//
//        // Autoriser underscore uniquement pour séparer l'indicatif
//        String regex = "^(\\+|00)?\\d{1,4}_\\d{8,15}$|^\\d{8,15}$";
//        return !cleaned.matches(regex);
//    }


    public boolean isEmailInvalid(String email) {
        if (email != null && !email.isEmpty()) {
            return email.isBlank() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        }
        return true;
    }

    public boolean isQuartierInvalid(String quartier) {
        return isBlank(quartier);
    }

    public boolean isNationaliteInvalid(String nat) {
        return isBlank(nat);
    }

    public boolean isNumeroCnssInvalid(String cnss) {
        return isBlank(cnss);
    }

    public boolean isProfessionInvalid(String profession) {
        if(profession != null && !profession.trim().isEmpty()) {
            return isBlank(profession);
        }
        return true;
    }
/*
//    public boolean isNumeroCnssExist(String numeroCnss) {
//        return employeRepository.existsByNumeroCnss(numeroCnss);
//    }
*/

    public boolean isEmployeValide(Employe e) {
        return !isMatriculeInvalid(e.getMatricule())
                && !isMatriculeExist(e.getMatricule())
                && !isTitreInvalid(e)
                && !isNomInvalid(e.getNom())
                && !isPrenomInvalid(e.getPrenom())
                && !isSexeInvalid(e)
                && !isDateNaissanceInvalid(e)
                && !isLieuNaissanceInvalid(e.getLieu_naissance())
                && !isSituationMatrimonialeInvalid(e)
                && !isNumeroIfuInvalid(e.getNumero_ifu())
              //  && !isTelephoneInvalid(e.getTelephone())
                && !isEmailInvalid(e.getEmail())
                && !isQuartierInvalid(e.getQuartier())
                && !isNationaliteInvalid(e.getNationalite())
                && !isNumeroCnssInvalid(e.getNumero_cnss())
                && !isProfessionInvalid(e.getProfession());
    }

    public boolean isEmployeValide1(Employe e) {
        return !isMatriculeInvalid(e.getMatricule())
                && !isTitreInvalid(e)
                && !isNomInvalid(e.getNom())
                && !isPrenomInvalid(e.getPrenom())
                && !isSexeInvalid(e)
                && !isDateNaissanceInvalid(e)
                && !isLieuNaissanceInvalid(e.getLieu_naissance())
                && !isSituationMatrimonialeInvalid(e)
                && !isNumeroIfuInvalid(e.getNumero_ifu())
                //  && !isTelephoneInvalid(e.getTelephone())
                && !isEmailInvalid(e.getEmail())
                && !isQuartierInvalid(e.getQuartier())
                && !isNationaliteInvalid(e.getNationalite())
                && !isNumeroCnssInvalid(e.getNumero_cnss())
                && !isProfessionInvalid(e.getProfession());
    }


    public List<ErrorResponse> getInvalidFieldMessages(Employe e) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (isMatriculeInvalid(e.getMatricule())) errors.add( new ErrorResponse ("matricule","Numéro Matricule manquant"));
        if (isMatriculeExist(e.getMatricule())) errors.add( new ErrorResponse ("matricule_existe","Ce numéro matricule existe déjà"));
        if (isTitreInvalid(e)) errors.add(new ErrorResponse("titre","Titre invalide") );
        if (isNomInvalid(e.getNom())) errors.add(new ErrorResponse("nom","Nom invalide"));
        if (isPrenomInvalid(e.getPrenom())) errors.add(new ErrorResponse("prenom","Prénom invalide"));
        if (isSexeInvalid(e)) errors.add(new ErrorResponse("sexe","Sexe invalide. Valeurs autorisées : MASCULIN, FEMININ"));
        if (isSituationMatrimonialeInvalid(e)) errors.add(new ErrorResponse("situation_matrimoniale","Situation matrimoniale invalide. Valeurs autorisées : CELIBATAIRE_SANS_ENFANT,CELIBATAIRE_AVEC_ENFANT, MARIE, DIVORCE, etc."));
        if (isDateNaissanceInvalid(e)) errors.add(new ErrorResponse("date_naissance","Date de naissance manquante"));
        if (isLieuNaissanceInvalid(e.getLieu_naissance())) errors.add(new ErrorResponse("lieu_naissance","Lieu de naissance manquant"));
        if (isNumeroIfuInvalid(e.getNumero_ifu())) errors.add(new ErrorResponse("numero_ifu","Numéro IFU manquant"));
       // if (isTelephoneInvalid(e.getTelephone())) errors.add(new ErrorResponse("telephone","Téléphone vide ou invalide"));
        if (isEmailInvalid(e.getEmail())) errors.add( new ErrorResponse("email","Email invalide"));
        if (isQuartierInvalid(e.getQuartier())) errors.add(new ErrorResponse("quartier","Quartier manquant"));
        if (isNationaliteInvalid(e.getNationalite())) errors.add(new ErrorResponse("nationalite","Nationalite invalide"));
        if (isNumeroCnssInvalid(e.getNumero_cnss())) errors.add(new ErrorResponse("numero_cnss","Numero de cnss invalide"));
        if (isNumeroCnssExist(e.getNumero_cnss())) errors.add( new ErrorResponse ("numero_cnss_existe","Ce numéro CNSS existe déjà"));

        // Ajout de la vérification d’unicité du numero_cnss
//

        //if (isProfessionInvalid(e.getProfession())) errors.add(new ErrorResponse("profession","Profession invalide"));

        return errors;
    }

    public List<ErrorResponse> getInvalidFieldMessages1(Employe e) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (isMatriculeInvalid(e.getMatricule())) errors.add( new ErrorResponse ("matricule","Numéro Matricule manquant"));
      if (isTitreInvalid(e)) errors.add(new ErrorResponse("titre","Titre invalide") );
        if (isNomInvalid(e.getNom())) errors.add(new ErrorResponse("nom","Nom invalide"));
        if (isPrenomInvalid(e.getPrenom())) errors.add(new ErrorResponse("prenom","Prénom invalide"));
        if (isSexeInvalid(e)) errors.add(new ErrorResponse("sexe","Sexe invalide. Valeurs autorisées : MASCULIN, FEMININ"));
        if (isSituationMatrimonialeInvalid(e)) errors.add(new ErrorResponse("situation_matrimoniale","Situation matrimoniale invalide. Valeurs autorisées : CELIBATAIRE_SANS_ENFANT,CELIBATAIRE_AVEC_ENFANT, MARIE, DIVORCE, etc."));
        if (isDateNaissanceInvalid(e)) errors.add(new ErrorResponse("date_naissance","Date de naissance manquante"));
        if (isLieuNaissanceInvalid(e.getLieu_naissance())) errors.add(new ErrorResponse("lieu_naissance","Lieu de naissance manquant"));
        if (isNumeroIfuInvalid(e.getNumero_ifu())) errors.add(new ErrorResponse("numero_ifu","Numéro IFU manquant"));
        // if (isTelephoneInvalid(e.getTelephone())) errors.add(new ErrorResponse("telephone","Téléphone vide ou invalide"));
        if (isEmailInvalid(e.getEmail())) errors.add( new ErrorResponse("email","Email invalide"));
        if (isQuartierInvalid(e.getQuartier())) errors.add(new ErrorResponse("quartier","Quartier manquant"));
        if (isNationaliteInvalid(e.getNationalite())) errors.add(new ErrorResponse("nationalite","Nationalite invalide"));
        if (isNumeroCnssInvalid(e.getNumero_cnss())) errors.add(new ErrorResponse("numero_cnss","Numero de cnss invalide"));

        // Ajout de la vérification d’unicité du numero_cnss
//

        if (isProfessionInvalid(e.getProfession())) errors.add(new ErrorResponse("profession","Profession invalide"));

        return errors;
    }


}
