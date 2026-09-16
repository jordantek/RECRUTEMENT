package com.tpc.tpcgestpaie.localapp.util;
import com.fasterxml.jackson.annotation.JsonValue;

public class GlobalEnums {
    public enum Status {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        SUSPENDED("Suspendu"),
        DELETED("Supprimé"),
        PENDING("En attente"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        ARCHIVED("Archivé"),
        DRAFT("Brouillon"),
        VALIDATED("Validé"),
        CANCELLED("Annulé"),
        CLOSED("Clôturé"),
        OPEN("Ouvert"),
        IN_PROCESS("En cours"),
        COMPLETED("Terminé"),
        FAILED("Échoué"),
        ON_HOLD("En pause"),
        EXPIRED("Expiré"),
        RENEWED("Renouvelé"),
        TEMPORARY("Temporaire"),
        PERMANENT("Permanent"),
        SICK_LEAVE("Congé maladie"),
        MATERNITY_LEAVE("Congé maternité"),
        PATERNITY_LEAVE("Congé paternité"),
        UNPAID_LEAVE("Congé sans solde"),
        PAID_LEAVE("Congé payé"),
        TERMINATED("Résilié"),
        RETIRED("Retraité"),
        BLOCKED("Bloqué"),
        UNDER_REVIEW("En révision"),
        CONTRAT_EN_COURS("CONTRAT EN COURS");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        @JsonValue // utilisé lors de la sérialisation JSON
        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum RoleName {
        ROLE_SUPER_ADMIN  ,
        ROLE_COMPANY_ADMIN,
        ROLE_HR,
        ROLE_MANAGER,
        ROLE_EMPLOYEE;

        public static boolean isValidRole(String roleName) {
            if (roleName == null) return false;
            roleName = roleName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (RoleName role : RoleName.values()) {
                if (role.name().equals(roleName)) {
                    return true;
                }
            }
            return false;
        }

    }

    public  enum Sexe{
        MASCULIN,
        FEMININ;

        public static boolean isValidSexe(String sexeName) {
            if (sexeName == null) return false;
            sexeName = sexeName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (Sexe sexe : Sexe.values()) {
                if (sexe.name().equals(sexeName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public  enum MaritalSatus{
        CELIBATAIRE_SANS_ENFANT,
        CELIBATAIRE_AVEC_ENFANT,
        MARIE,
        DIVORCE;

        public static boolean isValidMaritalSatus(String maritalSatusName) {
            if (maritalSatusName == null) return false;
            maritalSatusName = maritalSatusName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (MaritalSatus maritalSatus : MaritalSatus.values()) {
                if (maritalSatus.name().equals(maritalSatusName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public  enum Titres {
        MONSIEUR,
        MADAME,
        MADEMOISELLE;

        public static boolean isValidTitres(String titresName) {
            if (titresName == null) return false;
            titresName = titresName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (Titres titres : Titres.values()) {
                if (titres.name().equals(titresName)) {
                    return true;
                }
            }
            return false;
        }
    }

    // Type contrat
    public  enum TypeContrat{
        CDI,
        CDD;
        public static boolean isValidTypeContrat(String typeContratName) {
            if (typeContratName == null) return false;
            typeContratName = typeContratName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (TypeContrat typeContrat : TypeContrat.values()) {
                if (typeContrat.name().equals(typeContratName)) {
                    return true;
                }
            }
            return false;
        }
    }

    //Mouvement contrat
    public  enum MouvementContat{
        NOUVEAU,
        RENOUVELLEMENT;
        public static boolean isValidMouvementContat(String mouvementContatName) {
            if (mouvementContatName == null) return false;
            mouvementContatName = mouvementContatName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (MouvementContat mouvementContat : MouvementContat.values()) {
                if (mouvementContat.name().equals(mouvementContatName)) {
                    return true;
                }
            }
            return false;
        }
    }

    //Nature Rubrique ou élément calcul salaire
    public  enum NatureECS{
        AVANTAGE,
        RETENUE;
        public static boolean isValidNatureECS(String natureECSName) {
            if (natureECSName == null) return false;
            natureECSName = natureECSName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (NatureECS natureECS : NatureECS.values()) {
                if (natureECS.name().equals(natureECSName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public  enum ModeJouissanceConge{
        NUMERAIRE,
        REEL,
        DIFFERE,
        EPARGNE;
        public static boolean isValidModeJouissanceConge(String modeJouissanceCongeName) {
            if (modeJouissanceCongeName == null) return false;
            modeJouissanceCongeName = modeJouissanceCongeName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (ModeJouissanceConge modeJouissanceConge : ModeJouissanceConge.values()) {
                if (modeJouissanceConge.name().equals(modeJouissanceCongeName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum ConditionAcceptationConge {
        A_DEDUIRE_DES_CONGES,
        A_DEDUIRE_DU_SALAIRE_DE_PRESENCE,
        SANS_CONDITION;
//        NON_DEFINI;  // Valeur par défaut pour les anciennes données

        public static boolean isValidConditionAcceptationConge(String conditionAcceptationCongeName) {
            if (conditionAcceptationCongeName == null || conditionAcceptationCongeName.trim().isEmpty()) {
                return false;
            }
            conditionAcceptationCongeName = conditionAcceptationCongeName.trim().toUpperCase();
            for (ConditionAcceptationConge conditionAcceptationConge : ConditionAcceptationConge.values()) {
                if (conditionAcceptationConge.name().equals(conditionAcceptationCongeName)) {
                    return true;
                }
            }
            return false;
        }

        // Méthode pour parser avec valeur par défaut
//        public static ConditionAcceptationConge fromString(String value) {
//            if (value == null || value.trim().isEmpty()) {
//                return NON_DEFINI;
//            }
//            try {
//                return valueOf(value.trim().toUpperCase());
//            } catch (IllegalArgumentException e) {
//                return NON_DEFINI;
//            }
//        }
    }

    public  enum TypeAbsence{
        NUMERAIRE,
        REEL,
        DIFFERE,
        EPARGNE;
        public static boolean isValidModeJouissanceConge(String modeJouissanceCongeName) {
            if (modeJouissanceCongeName == null) return false;
            modeJouissanceCongeName = modeJouissanceCongeName.trim().toUpperCase(); // Nettoyer et uniformiser

            for (ModeJouissanceConge modeJouissanceConge : ModeJouissanceConge.values()) {
                if (modeJouissanceConge.name().equals(modeJouissanceCongeName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public  enum BoolValid {
        OUI,
        NON;

        public static boolean BoolValid(String name) {
            if (name == null) return false;
            name = name.trim().toUpperCase(); // Nettoyer et uniformiser

            for (BoolValid boolValid : BoolValid.values()) {
                if (boolValid.name().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

}
