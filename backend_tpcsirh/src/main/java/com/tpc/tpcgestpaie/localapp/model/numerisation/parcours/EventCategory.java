package com.tpc.tpcgestpaie.localapp.model.numerisation.parcours;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_event_categories")
@Data
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // ex: "SALAIRE", "CONTRAT", "SANCTION"

    @Column(nullable = false)
    private String label; // ex: "4. ÉVOLUTION SALARIALE"

    private String description;

    private Integer displayOrder; // Pour l'ordre sur la fiche PDF

    // Champ stratégique pour ton OCR
    @Column(columnDefinition = "TEXT")
    private String searchKeywords; // "salaire, brut, net, augmentation, prime"
}