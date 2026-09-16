package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.MontantRubrique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MontantRubriqueRepository extends JpaRepository<MontantRubrique, Long> {

    // Exemple de méthode pour rechercher par moisRubrique
    List<MontantRubrique> findByMoisRubrique(String moisRubrique);

    Optional<MontantRubrique> findByEmployeIdAndMoisRubriqueAndRubriqueId(@Param("employeId") Long employeId, @Param("moisRubrique") String moisRubrique,@Param("rubriqueId") Long rubriqueId);

    Optional<MontantRubrique> findFirstByEmployeIdAndMoisRubrique(
            @Param("employeId") Long employeId,
            @Param("moisRubrique") String moisRubrique
    );

    // Recherche par contrat employé
    List<MontantRubrique> findByContratEmployeId(Long contratEmployeId);

    // Recherche par employé
    List<MontantRubrique> findByEmployeId(Long employeId);

    // Recherche par company
    List<MontantRubrique> findByCompanyId(Long companyId);

    // Recherche par rubrique
    List<MontantRubrique> findByRubriqueId(Long rubriqueId);

    // Recherche par moisRubrique et company
    List<MontantRubrique> findByMoisRubriqueAndCompanyId(String moisRubrique, Long companyId);

    // Recherche par dateRubrique
    List<MontantRubrique> findByDateRubriqueBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

//    @Query("SELECT m FROM MontantRubrique m " +
//            "WHERE m.company.id = :companyId " +
//            "AND m.moisRubrique = :moisRubrique")
//    List<MontantRubrique> findByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
//                                                         @Param("moisRubrique") String moisRubrique);


    @Query("SELECT m FROM MontantRubrique m " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisRubrique = :moisRubrique " +
            "AND m.rubrique.libelle <> 'SALAIRE 13e MOIS'" +
            "AND m.rubrique.libelle <> 'PRIMES EXCEPTIONNELLES'" +
            "AND m.rubrique.libelle <> 'SALAIRE MOYEN'")
    List<MontantRubrique> findByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
                                                         @Param("moisRubrique") String moisRubrique);

    @Query("SELECT m FROM MontantRubrique m " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisRubrique = :moisRubrique " +
            "AND m.rubrique.libelle = 'SALAIRE 13e MOIS'")
    List<MontantRubrique> findSalaire13eMoisByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
                                                                       @Param("moisRubrique") String moisRubrique);

    @Query("SELECT m FROM MontantRubrique m " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisRubrique = :moisRubrique " +
            "AND m.rubrique.libelle = 'SALAIRE MOYEN'")
    List<MontantRubrique> findSalaireMoyenByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
                                                                       @Param("moisRubrique") String moisRubrique);


    @Query("SELECT m FROM MontantRubrique m " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisRubrique = :moisRubrique " +
            "AND m.rubrique.libelle = 'SALAIRE MOYEN JOURNALIER'")
    List<MontantRubrique> findSalaireMoyenJournalierByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
                                                                     @Param("moisRubrique") String moisRubrique);
    @Query("SELECT m FROM MontantRubrique m " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisRubrique = :moisRubrique " +
            "AND m.rubrique.libelle = 'PRIMES EXCEPTIONNELLES'")
    List<MontantRubrique> findPrimesExceptionnellesByCompanyIdAndMoisRubrique(@Param("companyId") Long companyId,
                                                                       @Param("moisRubrique") String moisRubrique);




}
