package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ContratEmployeRubrique;
import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContratEmployeRubriqueRepository extends JpaRepository<ContratEmployeRubrique, Long> {

    @EntityGraph(attributePaths = {"contratEmploye", "rubrique", "added_by"})
    Optional<ContratEmployeRubrique> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {"contratEmploye", "rubrique", "added_by"})
    List<ContratEmployeRubrique> findByContratEmployeId(Long contratEmployeId);

    @EntityGraph(attributePaths = {"contratEmploye", "rubrique", "added_by"})
    @Query("SELECT cer FROM ContratEmployeRubrique cer WHERE cer.contratEmploye.company.id = :id")
    List<ContratEmployeRubrique> findByCompanyId(@Param("id") Long id);

    @Query("""
    SELECT ce.id, e.id, e.nom, e.prenom, SUM(cer.montant), SUM(cer.montant_ajout)
    FROM ContratEmployeRubrique cer
    JOIN cer.contratEmploye ce
    JOIN ce.employe e
    WHERE cer.company.id = :companyId
    GROUP BY ce.id, e.id, e.nom, e.prenom
""")
    List<Object[]> findMontantsRawByContratAndEmploye(@Param("companyId") Long companyId);


    @Query("""
    SELECT 
        cer.rubrique.id,
        cer.rubrique.libelle,
        COALESCE(cer.montant, 0), 
        (COALESCE(cer.montant, 0) + COALESCE(cer.montant_ajout, 0))
    FROM ContratEmployeRubrique cer
    WHERE cer.contratEmploye.id = :contratId
""")
    List<Object[]> findRubriquesMontantsRawByContratId(@Param("contratId") Long contratId);

    Optional<ContratEmployeRubrique> findByContratEmployeIdAndRubriqueIdAndCompanyId(Long contratEmployeId, Long rubriqueId, Long companyId);

    @Query("SELECT DISTINCT cer.rubrique FROM ContratEmployeRubrique cer WHERE cer.company.id = :companyId")
    List<Rubrique> findDistinctRubriquesByCompanyId(@Param("companyId") Long companyId);


//    @Query("SELECT r FROM Rubrique r WHERE r.id NOT IN (" +
//            "SELECT DISTINCT cer.rubrique.id FROM ContratEmployeRubrique cer WHERE cer.company.id = :companyId" +
//            ")")
//    List<Rubrique> findRubriquesVariablesByCompanyId(@Param("companyId") Long companyId);

    @Query("""
    SELECT r FROM Rubrique r
    WHERE r.id NOT IN (
        SELECT DISTINCT cer.rubrique.id
        FROM ContratEmployeRubrique cer
        WHERE cer.company.id = :companyId
    )
    AND r.libelle NOT IN (
        
        'SALAIRE 13e MOIS',
        'PRIMES EXCEPTIONNELLES'
    )
""")
    List<Rubrique> findRubriquesVariablesByCompanyId(
            @Param("companyId") Long companyId
    );

    Optional<ContratEmployeRubrique> findByContratEmployeIdAndRubriqueId(Long contratId, Long rubriqueId);
}
