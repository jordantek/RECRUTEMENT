package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.TypeJourFerie;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO pour la création d'un jour férié
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerieCreateDTO {

    /**
     * Code unique du jour férié (ex: ANNIVERSAIRE_ENTREPRISE)
     * Optionnel, sera généré automatiquement si non fourni
     */
    private String slug;

    /**
     * Libellé du jour férié (obligatoire)
     */
    private String libelle;

      /**
     * Date du jour férié (obligatoire)
     */
    private LocalDate dateFerie;

    /**
     * Est-ce un jour férié mobile ?
     */
    private Boolean estFixe = false;

    /**
     * Est-ce récurrent chaque année ?
     */
    private Boolean estRecurrent = false;

    private String pays;
}