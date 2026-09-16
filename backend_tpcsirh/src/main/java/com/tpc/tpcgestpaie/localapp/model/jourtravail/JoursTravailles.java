package com.tpc.tpcgestpaie.localapp.model.jourtravail;

import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "jours_travailles")
@Data
public class JoursTravailles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "lundi", nullable = false)
    private boolean lundi = true;

    @Column(name = "mardi", nullable = false)
    private boolean mardi = true;

    @Column(name = "mercredi", nullable = false)
    private boolean mercredi = true;

    @Column(name = "jeudi", nullable = false)
    private boolean jeudi = true;

    @Column(name = "vendredi", nullable = false)
    private boolean vendredi = true;

    @Column(name = "samedi", nullable = false)
    private boolean samedi = false;

    @Column(name = "dimanche", nullable = false)
    private boolean dimanche = false;

    @Column(name = "jours_semaine")
    private Integer joursSemaine;

    @PrePersist
    @PreUpdate
    private void calculerJoursSemaine() {
        this.joursSemaine = compterJoursTravailles();
    }

    public int compterJoursTravailles() {
        int count = 0;
        if (lundi) count++;
        if (mardi) count++;
        if (mercredi) count++;
        if (jeudi) count++;
        if (vendredi) count++;
        if (samedi) count++;
        if (dimanche) count++;
        return count;
    }

    public boolean estJourTravaille(String jour) {
        return switch (jour.toLowerCase()) {
            case "lundi" -> lundi;
            case "mardi" -> mardi;
            case "mercredi" -> mercredi;
            case "jeudi" -> jeudi;
            case "vendredi" -> vendredi;
            case "samedi" -> samedi;
            case "dimanche" -> dimanche;
            default -> false;
        };
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (lundi) sb.append("Lun ");
        if (mardi) sb.append("Mar ");
        if (mercredi) sb.append("Mer ");
        if (jeudi) sb.append("Jeu ");
        if (vendredi) sb.append("Ven ");
        if (samedi) sb.append("Sam ");
        if (dimanche) sb.append("Dim ");
        return sb.toString().trim();
    }
}