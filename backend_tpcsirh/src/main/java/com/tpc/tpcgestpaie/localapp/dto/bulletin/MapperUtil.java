package com.tpc.tpcgestpaie.localapp.dto.bulletin;

import java.time.LocalDateTime;
import java.util.Map;

public class MapperUtil {

    public static BulletinPaieSageGenerateDTO mapToBulletin(Map<String, Object> row) {
        BulletinPaieSageGenerateDTO dto = new BulletinPaieSageGenerateDTO();

        dto.setIdBulletin(getLong(row, "id_bulletin_paie"));
        dto.setAutreAvantage(getDouble(row, "autre_avantange_bulletin_paie"));
        dto.setAutreRetenue(getDouble(row, "autre_retenue_bulletin_paie"));
        dto.setMois((String) row.get("mois_bulletin_paie"));
        dto.setMontantCnss(getDouble(row, "montant_cnss_bulletin_paie"));
        dto.setMontantCnssEmployeur(getDouble(row, "montant_cnss_employeur_bulletin_paie"));
        dto.setMontantIpts(getDouble(row, "montant_ipts_bulletin_paie"));
        dto.setMontantVps(getDouble(row, "montant_vps_bulletin_paie"));
        dto.setNetAPayer(getDouble(row, "net_a_payer_bulletin_paie"));
        dto.setNombreEnfant(getInteger(row, "nombre_enfant"));
        dto.setNombreJourOuHeureTravail(getDouble(row, "nombreJourOuHeureTravail"));
        dto.setSalaireBrut(getDouble(row, "salaire_brut_bulletin_paie"));
        dto.setSalaireNet(getDouble(row, "salaire_net_bulletin_paie"));
        dto.setTotalChargePatronale(getDouble(row, "total_charge_patronale_bulletin_paie"));
        dto.setTotalRetenue(getDouble(row, "total_retenue_bulletin_paie"));
        dto.setIdContratEmploye(getLong(row, "id_contrat_employe"));
        dto.setIdEmploye(getLong(row, "id_employe"));
        dto.setIdEntreprise(getLong(row, "id_entreprise"));
        dto.setSalaireBrutArrondi(getDouble(row, "salaire_brut_arrondi_bulletin_paie"));
        dto.setDateCalculSalaire(getLocalDateTime(row, "date_Calcul_salaire"));
        dto.setNumeroCompteEmploye((String) row.get("numero_compte_employe"));
        dto.setIdBanque(getLong(row, "id_banque"));
        dto.setMontantAib(getDouble(row, "montant_aib_bulletin_paie"));
        dto.setIdDepartement(getLong(row, "id_departement"));
        dto.setCreatedDate(getLocalDateTime(row, "created_date"));
        dto.setLastUpdate(getLocalDateTime(row, "last_update"));
        dto.setIdUser(getLong(row, "id_user"));
        dto.setSignataire((String) row.get("signataire"));
        dto.setCongePris(getDouble(row, "conge_pris"));
        dto.setSoldeConge(getDouble(row, "solde_conge"));

        return dto;
    }

    private static Long getLong(Map<String, Object> row, String key) {
        Object val = row.get(key);
        return val == null ? null : ((Number) val).longValue();
    }

    private static Integer getInteger(Map<String, Object> row, String key) {
        Object val = row.get(key);
        return val == null ? null : ((Number) val).intValue();
    }

    private static Double getDouble(Map<String, Object> row, String key) {
        Object val = row.get(key);
        return val == null ? null : ((Number) val).doubleValue();
    }

    private static LocalDateTime getLocalDateTime(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) return null;
        if (val instanceof LocalDateTime) return (LocalDateTime) val;
        if (val instanceof java.sql.Timestamp) return ((java.sql.Timestamp) val).toLocalDateTime();
        return null;
    }
}
