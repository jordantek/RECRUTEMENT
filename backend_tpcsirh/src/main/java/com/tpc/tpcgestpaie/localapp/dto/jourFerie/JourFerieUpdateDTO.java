package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour d'un jour férié
 * Tous les champs sont optionnels
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerieUpdateDTO {

    /**
     * Nouveau libellé (optionnel)
     */
    private String libelle;

     /**
     * Nouvelle date (optionnel)
     */
    private LocalDate dateFerie;

    /**
     * Nouvelle valeur pour estRecurrent (optionnel)
     */
    private Boolean estRecurrent;
    private String pays;
}