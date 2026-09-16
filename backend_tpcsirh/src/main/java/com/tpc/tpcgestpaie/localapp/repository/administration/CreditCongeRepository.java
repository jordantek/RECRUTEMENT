package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.CreditConge;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditCongeRepository extends JpaRepository<CreditConge, Long> {

    Optional<CreditConge> findByContratEmployeIdAndStatut(Long contratEmployeId, String statut);

    // Chargement avec les relations clés pour éviter les N+1
    @EntityGraph(attributePaths = {
            "contratEmploye",
            "employe",
            "company",
            "added_by"
    })
    List<CreditConge> findAll();

    @EntityGraph(attributePaths = {
            "contratEmploye",
            "employe",
            "company",
            "added_by"
    })
    Optional<CreditConge> findById(Long id);

    // Exemple : retrouver tous les crédits congés d’un employé avec fetch
    @EntityGraph(attributePaths = {
            "contratEmploye",
            "employe",
            "company",
            "added_by"
    })
    List<CreditConge> findByEmployeId(Long employeId);

    @EntityGraph(attributePaths = {
            "contratEmploye",
            "employe",
            "company",
            "added_by"
    })
    List<CreditConge> findByCompanyId(Long companyId);

    @EntityGraph(attributePaths = {
            "contratEmploye",
            "employe",
            "company",
            "added_by"
    })
    List<CreditConge> findByDateReferenceBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM CreditConge c WHERE c.employe.id = :idEmploye AND c.statut = 'ACTIF' AND c.deleted_at IS NULL ORDER BY c.dateReference DESC")
    Optional<CreditConge> findActifByEmployeId(Long idEmploye);

    @Query("SELECT c FROM CreditConge c WHERE c.contratEmploye.employe.id = :idEmploye ORDER BY c.dateReference DESC")
    List<CreditConge> findAllByEmployeIdOrderByDateReferenceDesc(@Param("idEmploye") Long idEmploye);

    Optional<CreditConge> findByContratEmployeIdAndDateReferenceAndStatut(Long contratEmployeId, LocalDate dateReference, String statut);

}
