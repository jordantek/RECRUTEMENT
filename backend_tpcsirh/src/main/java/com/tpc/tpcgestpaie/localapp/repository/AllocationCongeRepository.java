package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.AllocationConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationCongeRepository extends JpaRepository<AllocationConge, Long> {

    Optional<AllocationConge> findByEmployeIdAndMoisCalculSalaire(Long employeId, String moisCalculSalaire);

    List<AllocationConge> findByCompanyId(Long companyId);

    // Supprimer ou commenter cette méthode car nom invalide
    // List<AllocationConge> getAllocationsByCompanyAndContrat(Long companyId, Long contratEmployeId);

    @Query("SELECT a FROM AllocationConge a JOIN FETCH a.employe e WHERE a.company.id = :companyId AND (:contratEmployeId IS NULL OR a.contratEmploye.id = :contratEmployeId)")
    List<AllocationConge> findAllocationsWithEmploye(
            @Param("companyId") Long companyId,
            @Param("contratEmployeId") Long contratEmployeId
    );

    @Query("SELECT a FROM AllocationConge a " +
            "LEFT JOIN FETCH a.employe e " +
            "LEFT JOIN FETCH a.contratEmploye c " +
            "LEFT JOIN FETCH a.company comp " +
            "WHERE comp.id = :companyId " +
            "AND (:contratEmployeId IS NULL OR c.id = :contratEmployeId)")
    List<AllocationConge> findAllocationsWithEmployeAndContrat(@Param("companyId") Long companyId,
                                                               @Param("contratEmployeId") Long contratEmployeId);

    @Query("SELECT a FROM AllocationConge a " +
            "LEFT JOIN FETCH a.employe e " +
            "LEFT JOIN FETCH a.contratEmploye c " +
            "LEFT JOIN FETCH a.company comp " +
            "WHERE comp.id = :companyId " +
            "AND  a.moisCalculSalaire =:moisCalculSalaire")
    List<AllocationConge> findByCompanyIdAndMoisCalculSalaire(Long companyId, String moisCalculSalaire);
}
