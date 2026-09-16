package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.Acompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface AcompteRepository extends JpaRepository<Acompte, Long> {

    @Query("""
            SELECT a
            FROM Acompte a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            """)
    List<Acompte> findAll();

    @Query("""
            SELECT a
            FROM Acompte a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE e.id = :employeId
            """)
    List<Acompte> findByEmployeId(@Param("employeId") Long employeId);

    @Query("""
            SELECT a
            FROM Acompte a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE c.id = :companyId
            """)
    List<Acompte> findByCompanyId(@Param("companyId") Long companyId);


    @Query("""
                SELECT a
                FROM Acompte a
                LEFT JOIN FETCH a.contratEmploye ce
                LEFT JOIN FETCH a.employe e
                LEFT JOIN FETCH a.company c
                LEFT JOIN FETCH a.added_by u
                WHERE  a.mois = :mois
            """)
    List<Acompte> findByMois(
            @Param("mois") YearMonth mois
    );

    @Query("""
            SELECT a
            FROM Acompte a
            LEFT JOIN FETCH a.contratEmploye ce
            LEFT JOIN FETCH a.employe e
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE e.id = :employeId
            AND a.mois = :mois
            """)
    List<Acompte> findByEmployeAndMois(@Param("employeId") Long employeId, @Param("mois") YearMonth mois);

    @Query("""
                SELECT a
                FROM Acompte a
                LEFT JOIN FETCH a.contratEmploye ce
                LEFT JOIN FETCH a.employe e
                LEFT JOIN FETCH a.company c
                LEFT JOIN FETCH a.added_by u
                WHERE c.id = :companyId
                AND a.mois = :mois
            """)
    List<Acompte> findByCompanyAndMois(
            @Param("companyId") Long companyId,
            @Param("mois") String mois
    );

}
