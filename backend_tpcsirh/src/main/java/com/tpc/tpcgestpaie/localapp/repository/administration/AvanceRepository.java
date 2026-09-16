package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.Avance;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvanceRepository extends JpaRepository<Avance, Long> {

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            """)
    List<Avance> findAll();

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE e.id = :employeId
            """)
    List<Avance> findByEmployeId(@Param("employeId") Long employeId);

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE c.id = :companyId
            """)
    List<Avance> findByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE a.moisDemarrage >= :start AND a.moisDemarrage <= :end
            """)
    List<Avance> findByMoisDemarrageBetween(
            @Param("start") YearMonth start,
            @Param("end") YearMonth end
    );

    @Query("""
                SELECT a
                FROM Avance a
                LEFT JOIN FETCH a.contratEmploye ce
                LEFT JOIN FETCH a.employe e
                LEFT JOIN FETCH a.company c
                LEFT JOIN FETCH a.added_by u
                WHERE a.moisFin >= :start AND a.moisFin <= :end
            """)
    List<Avance> findByMoisFinBetween(
            @Param("start") YearMonth start,
            @Param("end") YearMonth end
    );

    @Query("""
                SELECT a
                FROM Avance a
                LEFT JOIN FETCH a.contratEmploye ce
                LEFT JOIN FETCH a.employe e
                LEFT JOIN FETCH a.company c
                LEFT JOIN FETCH a.added_by u
                WHERE c.id = :companyId
                AND a.moisFin >= :start AND a.moisFin <= :end
            """)
    List<Avance> findByCompanyAndMoisFinBetween(
            @Param("companyId") Long companyId,
            @Param("start") YearMonth start,
            @Param("end") YearMonth end
    );

    @Query("""
                SELECT a
                FROM Avance a
                LEFT JOIN FETCH a.contratEmploye ce
                LEFT JOIN FETCH a.employe e
                LEFT JOIN FETCH a.company c
                LEFT JOIN FETCH a.added_by u
                WHERE c.id = :companyId AND a.moisDemarrage >= :start AND a.moisDemarrage <= :end
            """)
    List<Avance> findByCompanyAndMoisDemarrageBetween(
            @Param("companyId") Long companyId,
            @Param("start") YearMonth start,
            @Param("end") YearMonth end
    );

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE a.employe = :employe 
            AND a.moisDemarrage = :moisDemarrage 
            AND a.moisFin = :moisFin
            """)
    Optional<Avance> findByEmployeAndMoisDemarrageAndMoisFin(
            @Param("employe") Employe employe,
            @Param("moisDemarrage") YearMonth moisDemarrage,
            @Param("moisFin") YearMonth moisFin
    );

    @Query("""
            SELECT a
            FROM Avance a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE c.id = :companyId AND a.moisDemarrage <= :mois AND a.moisFin >= :mois
            """)
    List<Avance> findByCompanyIdAndMoisContaining(
            @Param("companyId") Long companyId,
            @Param("mois") YearMonth mois
    );


    // Dans AvanceRepository.java - AJOUTEZ cette méthode
    @Query("SELECT COUNT(a) > 0 FROM Avance a WHERE a.employe.id = :employeId AND a.moisDemarrage = :moisDemarrage AND a.moisFin = :moisFin")
    boolean existsByEmployeIdAndMoisDemarrageAndMoisFin(
            @Param("employeId") Long employeId,
            @Param("moisDemarrage") YearMonth moisDemarrage,
            @Param("moisFin") YearMonth moisFin
    );
}
