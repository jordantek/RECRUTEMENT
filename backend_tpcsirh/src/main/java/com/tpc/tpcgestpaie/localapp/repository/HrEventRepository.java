package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.HrEvent;
import com.tpc.tpcgestpaie.localapp.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HrEventRepository extends JpaRepository<HrEvent, Long> {

    // Méthode pour trouver un événement récurrent existant pour un employé
    Optional<HrEvent> findByCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
            Company company,
            Employe employee,
            String type
    );

    // Méthode pour un événement unique (non récurrent)
    Optional<HrEvent> findByCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
            Company company,
            Employe employee,
            ContratEmploye contrat,
            String type,
            LocalDate eventDate
    );


    // =========================
    // Récupération par entreprise
    // =========================
    List<HrEvent> findByCompanyAndDeletedAtIsNull(Company company);

    // =========================
    // Récupération des événements uniques entre deux dates
    // =========================
    List<HrEvent> findByCompanyAndRecurringFalseAndEventDateBetweenAndDeletedAtIsNull(
            Company company, LocalDate start, LocalDate end);

    // =========================
    // Récupération des événements récurrents (anniversaires) pour un mois donné
    // =========================
    List<HrEvent> findByCompanyAndRecurringTrueAndDeletedAtIsNull(Company company);

    // =========================
    // Optionnel : chercher par type
    // =========================
    List<HrEvent> findByTypeAndDeletedAtIsNull(String type);

    // 🔒 Doublon événement UNIQUE
    boolean existsByCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
            Company company,
            Employe employee,
            ContratEmploye contratEmploye,
            String type,
            LocalDate eventDate
    );

    // 🔒 Doublon événement RÉCURRENT (anniversaire)
    boolean existsByCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
            Company company,
            Employe employee,
            String type
    );

    boolean existsByIdNotAndCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
            Long id,
            Company company,
            Employe employee,
            ContratEmploye contratEmploye,
            String type,
            LocalDate eventDate
    );

    boolean existsByIdNotAndCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
            Long id,
            Company company,
            Employe employee,
            String type
    );

    /**
     *
     * @param companyIds
     * @param today
     * @param endDate
     * @return
     */
    @Query("""
        SELECT e
        FROM HrEvent e
        JOIN e.eventType et
        WHERE
            e.deletedAt IS NULL
            AND e.company.id IN :companyIds
            AND e.eventDate IS NOT NULL
            AND (
                COALESCE(e.actionRequired) = true
            )
            AND (
                (
                    e.recurring = false
                    AND e.eventDate >= :today
                    AND e.eventDate <= :endDate
                )
                OR
                (
                    e.recurring = true
                    AND
                    FUNCTION('MONTH', e.eventDate) = FUNCTION('MONTH', CURRENT_DATE)
                    AND
                    FUNCTION('DAY', e.eventDate) = FUNCTION('DAY', CURRENT_DATE)
                    AND (
                        e.endDate IS NULL
                        OR e.endDate >= :today
                    )
                )
            )
        """)
    List<HrEvent> findUpcomingActionRequiredEvents(
            @Param("companyIds") List<Long> companyIds,
            @Param("today") LocalDate today,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
SELECT e
FROM HrEvent e
WHERE 
    e.company.id IN :companyIds
    AND e.deletedAt IS NULL
    AND e.eventDate IS NOT NULL
    AND (
        (
         COALESCE( e.recurring) 
            = false
            AND e.eventDate BETWEEN :today AND :endDate
        )
        OR
        (
           COALESCE( e.recurring)= true
            AND (
                (
                    MONTH(e.eventDate) > MONTH(:today)
                    OR (
                        MONTH(e.eventDate) = MONTH(:today)
                        AND DAY(e.eventDate) >= DAY(:today)
                    )
                )
                AND (
                    MONTH(e.eventDate) < MONTH(:endDate)
                    OR (
                        MONTH(e.eventDate) = MONTH(:endDate)
                        AND DAY(e.eventDate) <= DAY(:endDate)
                    )
                )
            )
            AND (e.endDate IS NULL OR e.endDate >= :today)
        )
    )
""")
    List<HrEvent> findUpcomingEvents(
            @Param("companyIds") List<Long> companyIds,
            @Param("today") LocalDate today,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
        SELECT e
        FROM HrEvent e
        WHERE
            e.company.id IN :companyIds
            AND e.deletedAt IS NULL
            AND e.eventDate IS NOT NULL
            AND (
                (
                    e.recurring = false
                    AND YEAR(e.eventDate) = :year
                    AND MONTH(e.eventDate) = :month
                )
                OR
                (
                    e.recurring = true
                    AND MONTH(e.eventDate) = :month
                    AND (e.endDate IS NULL OR e.endDate >= :startOfMonth)
                )
            )
        ORDER BY e.eventDate ASC
    """)
    List<HrEvent> findEventsByMonthAndCompanies(
            @Param("companyIds") List<Long> companyIds,
            @Param("year") int year,
            @Param("month") int month,
            @Param("startOfMonth") LocalDate startOfMonth
    );

}
