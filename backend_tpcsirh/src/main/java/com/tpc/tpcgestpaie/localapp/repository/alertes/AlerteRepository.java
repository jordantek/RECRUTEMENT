package com.tpc.tpcgestpaie.localapp.repository.alertes;
import com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteAbsenceDTO;
import com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteContratDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AlerteRepository extends JpaRepository<Employe, Long> {

    @Query("""
    SELECT e
    FROM Employe e
    JOIN FETCH e.company c
    WHERE 
        FUNCTION('MONTH', e.date_naissance) = FUNCTION('MONTH', :targetDate)
        AND FUNCTION('DAY', e.date_naissance) = FUNCTION('DAY', :targetDate)
        AND e.deleted_at IS NULL
""")
    List<Employe> findAnniversairesDans(@Param("targetDate") LocalDate targetDate);

    @Query("""
    SELECT e
    FROM Employe e
    JOIN FETCH e.company c
    WHERE e.deleted_at IS NULL
      AND (
           (MONTH(e.date_naissance) = MONTH(:startDate) AND DAY(e.date_naissance) >= DAY(:startDate))
        OR (MONTH(e.date_naissance) = MONTH(:endDate)   AND DAY(e.date_naissance) <= DAY(:endDate))
        OR (MONTH(e.date_naissance) > MONTH(:startDate) AND MONTH(e.date_naissance) < MONTH(:endDate))
       )
""")
    List<Employe> findAnniversairesBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT new com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteContratDTO(
        c.id,
        c.type_contrat,
        c.date_debut,
        c.date_fin,
        c.fin_essai,
        e.id,
        e.matricule,
        e.nom,
        e.prenom,
        e.email,
        e.telephone,
        c.poste.libelle,
        c.departement.libelle,
        comp.id,
        comp.name
    )
    FROM ContratEmploye c
    JOIN c.employe e
    JOIN e.company comp
     WHERE (
          FUNCTION('DAYOFYEAR', c.date_fin) >= FUNCTION('DAYOFYEAR', :startDate)
          OR
          FUNCTION('DAYOFYEAR', c.date_fin) <= FUNCTION('DAYOFYEAR', :endDate)
      )
""")
    List<AlerteContratDTO> findContratsQuiArriventATermes(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
    SELECT new com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteContratDTO(
        c.id,
        c.type_contrat,
        c.date_debut,
        c.date_fin,
        c.fin_essai,
        e.id,
        e.matricule,
        e.nom,
        e.prenom,
        e.email,
        e.telephone,
        c.poste.libelle,
        c.departement.libelle,
        comp.id,
        comp.name
    )
    FROM ContratEmploye c
    JOIN c.employe e
    JOIN e.company comp
    WHERE c.fin_essai BETWEEN :startDate AND :endDate
""")
    List<AlerteContratDTO> findEssaiQuiArriventATermes(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Query("""
    SELECT new com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteAbsenceDTO(
        a.id,
        a.libelle,
        a.typeAbsence.libelle,
        a.dateDebut,
        a.dateFin,
        a.modeJouissance,
        CAST(a.duree AS integer),
        e.id,
        e.matricule,
        e.nom,
        e.prenom,
        e.email,
        e.telephone,
        ce.id,
        ce.type_contrat,
        ce.poste.libelle,
        ce.departement.libelle,
        c.id,
        c.name
    )
    FROM Absence a
    JOIN a.employe e
    JOIN e.company c
    JOIN a.contratEmploye ce
    WHERE a.deleted_at IS NULL
      AND a.dateDebut IS NOT NULL
      AND (
            (:startDate <= :endDate AND a.dateDebut BETWEEN :startDate AND :endDate)
            OR
            (:startDate > :endDate AND (a.dateDebut >= :startDate OR a.dateDebut <= :endDate))
          )
    ORDER BY a.dateDebut ASC
""")
    List<AlerteAbsenceDTO> findDebutAbsenceBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT new com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteAbsenceDTO(
        a.id,
        a.libelle,
        a.typeAbsence.libelle,
        a.dateDebut,
        a.dateFin,
        a.modeJouissance,
        CAST(a.duree AS integer),
        e.id,
        e.matricule,
        e.nom,
        e.prenom,
        e.email,
        e.telephone,
        ce.id,
        ce.type_contrat,
        ce.poste.libelle,
        ce.departement.libelle,
        c.id,
        c.name
    )
    FROM Absence a
    JOIN a.employe e
    JOIN e.company c
    JOIN a.contratEmploye ce
    WHERE a.deleted_at IS NULL
      AND a.dateDebut IS NOT NULL
      AND (
            (:startDate <= :endDate AND a.dateFin BETWEEN :startDate AND :endDate)
            OR
            (:startDate > :endDate AND (a.dateFin >= :startDate OR a.dateFin <= :endDate))
          )
    ORDER BY a.dateDebut ASC
""")
    List<AlerteAbsenceDTO> findFinAbsenceBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );




}
