package com.tpc.tpcgestpaie.localapp.repository.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.SuiviAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuiviAbsenceRepository extends JpaRepository<SuiviAbsence, Long> {

    Optional<SuiviAbsence> findByDemandeAbsenceId(Long demandeAbsenceId);

    // Absences dont le départ est prévu aujourd'hui et pas encore confirmé
    @Query("SELECT s FROM SuiviAbsence s WHERE s.dateDepartPrevue = :today AND s.departConfirme = false")
    List<SuiviAbsence> findDepartsAConfirmerAujourdhui(@Param("today") LocalDate today);

    // Absences dont le départ est prévu demain (notification J-1)
    @Query("SELECT s FROM SuiviAbsence s WHERE s.dateDepartPrevue = :tomorrow AND s.departConfirme = false")
    List<SuiviAbsence> findDepartsPrevusDemain(@Param("tomorrow") LocalDate tomorrow);

    // Absences dont le retour est prévu aujourd'hui et pas encore confirmé
    @Query("SELECT s FROM SuiviAbsence s WHERE s.dateRetourPrevue = :today AND s.retourConfirme = false")
    List<SuiviAbsence> findRetoursAConfirmerAujourdhui(@Param("today") LocalDate today);

    // Absences en cours (départ confirmé, retour pas encore confirmé)
    @Query("SELECT s FROM SuiviAbsence s WHERE s.departConfirme = true AND s.retourConfirme = false")
    List<SuiviAbsence> findAbsencesEnCours();
}