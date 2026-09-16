package com.tpc.tpcgestpaie.localapp.repository.custom;

import com.tpc.tpcgestpaie.localapp.model.Rubrique;

import java.util.List;

public interface RubriqueRepositoryCustom {
    Integer getDernierNumeroOrdre();
    List<Rubrique> getRubriqueStartByLibelle(String libelle);
    List<Rubrique> getAllRubriquePourEtatSalaire();
    List<Rubrique> getAllRubriquePourEnregistrementMontantMensuelRubrique();
}
