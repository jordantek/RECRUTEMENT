package com.tpc.tpcgestpaie.localapp.util;

import com.tpc.tpcgestpaie.localapp.model.Employe;

public class HrEventMessageBuilder {

    public static String buildTitle(String type, Employe emp) {
        String fullName = emp.getPrenom() + " " + emp.getNom();

        return switch (type) {

            case HrEventTypes.BIRTHDAY_EMPLOYEE ->
                    "🎂 Anniversaire – " + fullName;

            case HrEventTypes.CONTRACT_ANNIVERSARY ->
                    "📄 Anniversaire de contrat – " + fullName;

            case HrEventTypes.CONTRACT_END ->
                    "⏳ Fin de contrat – " + fullName;

            case HrEventTypes.TRIAL_PERIOD_END ->
                    "🧪 Fin de période d’essai – " + fullName;

            default ->
                    "📌 Événement RH – " + fullName;
        };
    }

    public static String buildDescription(String type, Employe emp) {
        String fullName = emp.getPrenom() + " " + emp.getNom();

        return switch (type) {

            case HrEventTypes.BIRTHDAY_EMPLOYEE ->
                    "Anniversaire de naissance de l’employé " + fullName + ".";

            case HrEventTypes.CONTRACT_ANNIVERSARY ->
                    "Anniversaire du contrat de travail de l’employé " + fullName + ".";

            case HrEventTypes.CONTRACT_END ->
                    "Le contrat de travail de l’employé " + fullName + " arrive à échéance à cette date.";

            case HrEventTypes.TRIAL_PERIOD_END ->
                    "La période d’essai de l’employé " + fullName + " prend fin à cette date.";

            default ->
                    "Un événement de gestion des ressources humaines concerne l’employé " + fullName + ".";
        };
    }
}
