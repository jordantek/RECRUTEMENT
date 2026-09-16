package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.repository.custom.RubriqueRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RubriqueRepository extends JpaRepository<Rubrique, Long>, RubriqueRepositoryCustom {

    // AJOUT: Pagination pour toutes les rubriques
    Page<Rubrique> findAll(Pageable pageable);

    // AJOUT: Pagination pour les rubriques par entreprise
    @Query("SELECT DISTINCT r FROM Rubrique r " +
            "JOIN ContratEmployeRubrique cer ON cer.rubrique.id = r.id " +
            "JOIN cer.contratEmploye ce " +
            "WHERE ce.company.id = :companyId " +
            "ORDER BY r.libelle")
    Page<Rubrique> findDistinctRubriquesByCompanyIdPaginated(@Param("companyId") Long companyId, Pageable pageable);

    // AJOUT: Pagination pour les rubriques variables par entreprise
    @Query("SELECT DISTINCT r FROM Rubrique r " +
            "JOIN ContratEmployeRubrique cer ON cer.rubrique.id = r.id " +
            "JOIN cer.contratEmploye ce " +
            "WHERE ce.company.id = :companyId " +
            "AND r.libelle NOT IN (" +
            "    'FORFAIT HEURES SUPPLEMENTAIRE', " +
            "    'AUGMENTATION SALARIALE', " +
            "    'INDEMNITES DE CONGES', " +
            "    'PRIME D''ANCIENNETE', " +
            "    'MENSUALITES', " +
            "    'AVANCE', " +
            "    'SALAIRE DE BASE', " +
            "    'HONORAIRE DE BASE', " +
            "    'TRANSFERT', " +
            "    'HONORAIRE BRUT IMPOSABLE'" +
            ") ORDER BY r.libelle")
    Page<Rubrique> findRubriquesVariablesByCompanyIdPaginated(@Param("companyId") Long companyId, Pageable pageable);

    Optional<Rubrique> findByLibelle(String libelle);

    boolean existsByLibelle(String libelle);

    @Query("SELECT r FROM Rubrique r WHERE r.libelle NOT IN (" +
            "    'FORFAIT HEURES SUPPLEMENTAIRE', " +
            "    'AUGMENTATION SALARIALE', " +
            "    'INDEMNITES DE CONGES', " +
            "    'PRIME D''ANCIENNETE', " +
            "    'MENSUALITES', " +
            "    'AVANCE', " +
            "    'SALAIRE DE BASE', " +
            "    'HONORAIRE DE BASE', " +
            "    'TRANSFERT', " +
            "    'HONORAIRE BRUT IMPOSABLE'" +
            ") ORDER BY r.id ASC")
    List<Rubrique> findAllVariableRubriques();

    @Query("SELECT r FROM Rubrique r WHERE r.libelle NOT IN (" +
            "    'FORFAIT HEURES SUPPLEMENTAIRE', " +
            "    'AUGMENTATION SALARIALE', " +
            "    'INDEMNITES DE CONGES', " +
            "    'PRIME D''ANCIENNETE', " +
//            "    'SALAIRE MOYEN', " +
            "    'SALAIRE 13e MOIS', " +
            "    'PRIMES EXCEPTIONNELLES', " +
            "    'HONORAIRE BRUT IMPOSABLE'" +
            ") ORDER BY r.id ASC")
    List<Rubrique> findAllRubriques();

    @Query("SELECT r.id FROM Rubrique r WHERE UPPER(r.libelle) = UPPER(:libelle)")
    Optional<Long> findIdByLibelle(@Param("libelle") String libelle);

    Optional<Rubrique> findByLibelleIgnoreCase(String libelle);


}
