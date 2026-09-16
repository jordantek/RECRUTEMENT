package com.tpc.tpcgestpaie.localapp.dto.bulletin;

import com.tpc.tpcgestpaie.localapp.model.BulletinSage;

import java.util.List;
import java.util.stream.Collectors;

public class BulletinPaieMapper {

    public static BulletinPaieGenerateDTO fromSageJson(BulletinSage sage) {
        if (sage == null) return null;

        BulletinPaieGenerateDTO dto = new BulletinPaieGenerateDTO();

        // Identification employé et contrat
        dto.setEmployeId(sage.getId_employe());
        dto.setContratEmployeId(sage.getId_contrat_employe());
        dto.setNumeroCompteEmploye(sage.getNumero_compte_employe());
        dto.setBanqueId(sage.getId_banque());
        dto.setCompanyId(sage.getId_entreprise());
        dto.setMois(sage.getMois_bulletin_paie());
        dto.setDateCalculSalaire(sage.getDate_Calcul_salaire());
        dto.setSignataire(sage.getSignataire());
        dto.setNombreEnfant(sage.getNombre_enfant());

        // Congés et temps
        dto.setSoldeConge(sage.getSolde_conge());
        dto.setCongePris(sage.getConge_pris());
        dto.setTempsTravail(sage.getNombreJourOuHeureTravail());

        // Salaires et primes
        dto.setSalaireBrut(sage.getSalaire_brut_bulletin_paie());
        dto.setSalaireBrutArrondi(sage.getSalaire_brut_arrondi_bulletin_paie());
        dto.setSalaireNet(sage.getSalaire_net_bulletin_paie());
        dto.setAutreAvantage(sage.getAutre_avantange_bulletin_paie());

        // Retenues et cotisations
        dto.setMontantCnss(sage.getMontant_cnss_bulletin_paie());
        dto.setMontantIpts(sage.getMontant_ipts_bulletin_paie());
        dto.setMontantVps(sage.getMontant_vps_bulletin_paie());
        dto.setMontantAib(sage.getMontant_aib_bulletin_paie());
        dto.setMontantCnssEmployeur(sage.getMontant_cnss_employeur_bulletin_paie());
        dto.setTotalChargePatronale(sage.getTotal_charge_patronale_bulletin_paie());
        dto.setTotalRetenue(sage.getTotal_retenue_bulletin_paie());
        dto.setNetAPayer(sage.getNet_a_payer_bulletin_paie());
        dto.setTotalAutreRetenue(sage.getAutre_retenue_bulletin_paie());

        // Champs front non présents dans le JSON Sage → valeurs par défaut ou calculées ailleurs
        dto.setNomPrenomEmploye("");
        dto.setMatriculeEmploye("");
        dto.setSalaireBase(0.0);
        dto.setPrimes(null);
        dto.setPrimesExceptionnelles(0.0);
        dto.setPrimes13eMois(0.0);
        dto.setSalaireButPrecedant(0.0);
        dto.setTotalChargeSalarialRetenue(0.0);
        dto.setTotalRetenueNet(0.0);
        dto.setMensualite(0.0);
        dto.setAvance(0.0);
        dto.setAcompte(0.0);
        dto.setNatureContrat("");
        dto.setTypeContrat("");
        dto.setFonction("");
        dto.setNumeroCnss("");
        dto.setNumeroCnssEmployeur("");
        dto.setDateDebut("");
        dto.setDateFin("");
        dto.setModePaiement("");
        dto.setQrCodeImg("");
        dto.setNomBanque("");
        dto.setNomEntreprise("");
        dto.setLogoEntreprise("");
        dto.setAdresse("");
        dto.setTelephone("");
        dto.setRccm("");
        dto.setMail("");
        dto.setSite("");
        dto.setQrCodeData("");

        return dto;
    }

    public static List<BulletinPaieGenerateDTO> fromSageJsonList(List<BulletinSage> sageList) {
        return sageList.stream()
                .map(BulletinPaieMapper::fromSageJson)
                .collect(Collectors.toList());
    }
}
