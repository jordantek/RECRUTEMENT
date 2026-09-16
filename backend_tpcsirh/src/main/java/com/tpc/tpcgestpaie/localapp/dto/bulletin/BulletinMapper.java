package com.tpc.tpcgestpaie.localapp.dto.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinIndependantResponse;
import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinPaieGenerateDTO;

public class BulletinMapper {

    public static BulletinPaieGenerateDTO toRhDTO(BulletinIndependantResponse source) {
        if (source == null) {
            return null;
        }

        BulletinPaieGenerateDTO dto = new BulletinPaieGenerateDTO();

        // 🔹 Identifiants
        dto.setEmployeId(source.getId_employe());
        dto.setContratEmployeId(source.getId_contrat_employe());
        dto.setCompanyId(source.getId_entreprise());
        dto.setBanqueId(source.getId_banque());
        dto.setNumeroCompteEmploye(source.getNumero_compte_employe());

        // 🔹 Infos salariales
        dto.setSalaireBrut(source.getSalaire_brut_bulletin_paie());
        dto.setSalaireBrutArrondi(source.getSalaire_brut_arrondi_bulletin_paie());
        dto.setSalaireNet(source.getSalaire_net_bulletin_paie());
        dto.setNetAPayer(source.getNet_a_payer_bulletin_paie());

        // 🔹 Charges et retenues
        dto.setMontantCnss(source.getMontant_cnss_bulletin_paie());
        dto.setMontantCnssEmployeur(source.getMontant_cnss_employeur_bulletin_paie());
        dto.setMontantIpts(source.getMontant_ipts_bulletin_paie());
        dto.setMontantVps(source.getMontant_vps_bulletin_paie());
        dto.setMontantAib(source.getMontant_aib_bulletin_paie());
        dto.setTotalRetenue(source.getTotal_retenue_bulletin_paie());
        dto.setTotalChargePatronale(source.getTotal_charge_patronale_bulletin_paie());
        dto.setTotalAutreRetenue(source.getAutre_retenue_bulletin_paie());

        // 🔹 Congés
        dto.setSoldeConge(source.getSolde_conge());
        dto.setCongePris(source.getConge_pris());

        // 🔹 Données générales
        dto.setMois(source.getMois_bulletin_paie());
        dto.setDateCalculSalaire(source.getDate_Calcul_salaire());
        dto.setNombreEnfant(source.getNombre_enfant());
        dto.setTempsTravail(source.getNombreJourOuHeureTravail());

        // 🔹 Champs additionnels disponibles
        dto.setAutreAvantage(source.getAutre_avantange_bulletin_paie());
        dto.setSignataire(source.getSignataire());

        // ⚠️ Champs du DTO non présents dans la sortie indépendante :
        // - nomPrenomEmploye, matriculeEmploye, fonction, typeContrat, etc.
        // Tu pourras les remplir plus tard si l’API externe les fournit ou
        // via une autre source (BD interne).

        return dto;
    }
}
