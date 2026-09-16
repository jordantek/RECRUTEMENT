package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.TypeJourFerie;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO de réponse pour un jour férié
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerieResponseDTO {

    private Long id;

    private String slug;

    private String libelle;

    private LocalDate dateFerie;
    private String pays;

    /**
     * Jour de la semaine en français (ex: "Lundi", "Samedi")
     */
    private String jourSemaine;

    /**
     * Indique si le jour férié tombe un weekend
     */
    private Boolean tombeEnWeekend;

   private Boolean estFixe;

    private Boolean estRecurrent;
}