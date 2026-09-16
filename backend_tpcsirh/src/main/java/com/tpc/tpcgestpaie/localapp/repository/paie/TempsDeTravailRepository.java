package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.TempsDeTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TempsDeTravailRepository extends JpaRepository<TempsDeTravail, Long> {

    // Trouver par company et mois
    List<TempsDeTravail> findByCompanyAndMois(Company company, String mois);

    // Trouver par company, mois et département via poste du contrat
    List<TempsDeTravail> findByCompanyAndMoisAndContratEmploye_Poste_Departement_Id(
            Company company,
            String mois,
            Long departementId
    );

    // Charger tout avec les associations principales
    @Query("""
        SELECT t
        FROM TempsDeTravail t
        LEFT JOIN FETCH t.employe
        LEFT JOIN FETCH t.contratEmploye ce
        LEFT JOIN FETCH ce.departement
        LEFT JOIN FETCH ce.poste
        LEFT JOIN FETCH ce.company
        LEFT JOIN FETCH t.company
    """)
    List<TempsDeTravail> findAll();

    // Trouver par employé et mois avec les associations
    @Query("""
        SELECT t
        FROM TempsDeTravail t
        LEFT JOIN FETCH t.employe
        LEFT JOIN FETCH t.contratEmploye ce
        LEFT JOIN FETCH ce.departement
        LEFT JOIN FETCH ce.poste
        LEFT JOIN FETCH ce.company
        LEFT JOIN FETCH t.company
        WHERE t.employe.id = :employeId AND t.mois = :mois
    """)
    Optional<TempsDeTravail> findByEmployeIdAndMois(
            @Param("employeId") Long employeId,
            @Param("mois") String mois
    );

    // Trouver par companyId avec fetch
    @Query("""
        SELECT t
        FROM TempsDeTravail t
        LEFT JOIN FETCH t.employe
        LEFT JOIN FETCH t.contratEmploye ce
        LEFT JOIN FETCH ce.departement
        LEFT JOIN FETCH ce.poste
        LEFT JOIN FETCH ce.company
        LEFT JOIN FETCH t.company c
        WHERE c.id = :companyId
        ORDER BY  t.mois DESC 
    """)
    List<TempsDeTravail> findByCompanyId(@Param("companyId") Long companyId);

    // Trouver par employé sans fetch (utile parfois)
    List<TempsDeTravail> findByEmployeId(Long employeId);

    // Trouver par company et département
    @Query("""
        SELECT t
        FROM TempsDeTravail t
        LEFT JOIN FETCH t.employe
        LEFT JOIN FETCH t.contratEmploye ce
        LEFT JOIN FETCH ce.departement d
        LEFT JOIN FETCH ce.poste
        LEFT JOIN FETCH ce.company
        LEFT JOIN FETCH t.company comp
        WHERE comp.id = :companyId AND d.id = :departementId
    """)
    List<TempsDeTravail> findAllByCompanyAndDepartement(
            @Param("companyId") Long companyId,
            @Param("departementId") Long departementId
    );

    @Query("SELECT t FROM TempsDeTravail t WHERE t.company.id = :companyId AND t.mois = :mois")
    List<TempsDeTravail> findByCompanyIdAndMois(@Param("companyId") Long companyId, @Param("mois") String mois);

}
