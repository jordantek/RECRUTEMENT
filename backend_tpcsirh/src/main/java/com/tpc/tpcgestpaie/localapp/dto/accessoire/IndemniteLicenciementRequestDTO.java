package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;
import java.util.List;

public class IndemniteLicenciementRequestDTO {
    private Long idContratEmploye;
    private Long idCompany;
    private Data data;

    public static class Data {
        private List<MontantMensuel> montantsMensuels;
        private BigDecimal montantTotal;
        private BigDecimal montantMoyen;
        private String typeLicencement;
        private String moisCalculSalaire; // "yyyy-MM"
        private BigDecimal indemniteSelonAnciennete;
        private double anciennete;

        // getters/setters
        public List<MontantMensuel> getMontantsMensuels() {
            return montantsMensuels;
        }
        public void setMontantsMensuels(List<MontantMensuel> montantsMensuels) {
            this.montantsMensuels = montantsMensuels;
        }
        public BigDecimal getMontantTotal() {
            return montantTotal;
        }
        public void setMontantTotal(BigDecimal montantTotal) {
            this.montantTotal = montantTotal;
        }
        public BigDecimal getMontantMoyen() {
            return montantMoyen;
        }

        public void setMontantMoyen(BigDecimal montantMoyen) {
            this.montantMoyen = montantMoyen;
        }

        public String getTypeLicencement() {
            return typeLicencement;
        }

        public void setTypeLicencement(String typeLicencement) {
            this.typeLicencement = typeLicencement;
        }

        public String getMoisCalculSalaire() {
            return moisCalculSalaire;
        }

        public void setMoisCalculSalaire(String moisCalculSalaire) {
            this.moisCalculSalaire = moisCalculSalaire;
        }

        public BigDecimal getIndemniteSelonAnciennete() {
            return indemniteSelonAnciennete;
        }

        public void setIndemniteSelonAnciennete(BigDecimal indemniteSelonAnciennete) {
            this.indemniteSelonAnciennete = indemniteSelonAnciennete;
        }

        public double getAnciennete() {
            return anciennete;
        }

        public void setAnciennete(double anciennete) {
            this.anciennete = anciennete;
        }
    }

    public static class MontantMensuel {
        private String mois; // "yyyy-MM"
        private BigDecimal montant;
        // getters/setters
        public String getMois() {
            return mois;
        }
        public void setMois(String mois) {
            this.mois = mois;
        }
        public BigDecimal getMontant() {
            return montant;
        }
        public void setMontant(BigDecimal montant) {
            this.montant = montant;
        }
    }
    // getters/setters
    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public Long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Long idCompany) {
        this.idCompany = idCompany;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
