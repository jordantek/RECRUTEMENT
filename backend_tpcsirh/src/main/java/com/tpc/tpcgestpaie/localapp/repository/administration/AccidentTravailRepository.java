package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.AccidentTravail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AccidentTravailRepository extends JpaRepository<AccidentTravail, Long> {

    // === MÉTHODES PAGINÉES ===

    @Query("SELECT a FROM AccidentTravail a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company")
    Page<AccidentTravail> findAllWithRelations(Pageable pageable);

    @Query("SELECT a FROM AccidentTravail a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "WHERE a.employe.id = :employeId")
    Page<AccidentTravail> findByEmployeId(@Param("employeId") Long employeId, Pageable pageable);

    @Query("SELECT a FROM AccidentTravail a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "WHERE a.company.id = :companyId")
    Page<AccidentTravail> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM AccidentTravail a " +
            "LEFT JOIN FETCH a.employe " +
            "LEFT JOIN FETCH a.contratEmploye " +
            "LEFT JOIN FETCH a.company " +
            "WHERE a.dateAccident BETWEEN :start AND :end")
    Page<AccidentTravail> findByDateAccidentBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);

    boolean existsByDateAccidentAndDateDeclaration(LocalDate dateAccident, LocalDate dateDeclaration);

    @Query("SELECT COUNT(a) FROM AccidentTravail a WHERE a.company.id = :companyId")
    Long countAccidentsTravail(@Param("companyId") Long companyId);

    // Méthodes de comptage pour la pagination
    @Query("SELECT COUNT(a) FROM AccidentTravail a WHERE a.employe.id = :employeId")
    long countByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT COUNT(a) FROM AccidentTravail a WHERE a.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM AccidentTravail a WHERE a.dateAccident BETWEEN :start AND :end")
    long countByDateAccidentBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}