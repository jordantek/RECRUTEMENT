package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.EmployeSuperieur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeSuperieurRepository extends JpaRepository<EmployeSuperieur, Long> {

    // ========================================
    // REQUÊTES EXISTANTES
    // ========================================

    Optional<EmployeSuperieur> findByContratEmployeIdAndIsActiveTrue(Long contratEmployeId);

    Optional<EmployeSuperieur> findByCompanyIdAndEmployeIdAndIsActiveTrue(Long companyId, Long employeId);

    List<EmployeSuperieur> findByCompanyIdAndIsActiveTrue(Long companyId);

    // ========================================
    // REQUÊTE CORRIGÉE : SUBORDONNÉS
    // ========================================

    /**
     * Récupère tous les employés subordonnés à un employé donné
     * Utilise une native query car JSON_SEARCH fonctionne mieux en SQL natif
     *
     * CORRIGÉ : Utilise directement la native query
     */
    @Query(value = """
        SELECT 
            e.id as employeId,
            e.matricule,
            e.nom,
            e.prenom,
            e.titre,
            e.sexe,
            e.telephone,
            e.email,
            e.date_naissance as dateNaissance,
            c.id as contratEmployeId,
            c.type_contrat as typeContrat,
            c.status_contrat as statusContrat,
            c.date_embauche as dateEmbauche,
            c.salaire_base as salaireBase,
            c.salaire_brut as salaireBrut,
            p.id as posteId,
            p.libelle as posteLibelle,
            d.id as departementId,
            d.libelle as departementLibelle,
            cat.name as categorieEmploye,
            comp.id as companyId,
            comp.name as companyName,
            es.is_active as isActive,
            es.created_at as createdAt,
            es.updated_at as updatedAt
        FROM employe_superieur es
        JOIN employes e ON e.id = es.employe_id
        JOIN contrat_employes c ON c.id = es.contrat_employe_id
        JOIN companies comp ON comp.id = es.company_id
        LEFT JOIN postes p ON p.id = c.poste_id
        LEFT JOIN departements d ON d.id = c.departement_id
        LEFT JOIN category_employes cat ON cat.id = c.category_employe_id
        WHERE es.is_active = true
        AND e.id != :superieurId
        AND JSON_SEARCH(
            es.hierarchie,
            'one',
            CAST(:superieurId AS CHAR),
            NULL,
            '$.superieurs[*].employe_superieur_id'
        ) IS NOT NULL
        ORDER BY e.nom ASC, e.prenom ASC
    """, nativeQuery = true)
    List<Object[]> findSubordonnesBySuperieurId(@Param("superieurId") Long superieurId);

    /**
     * Récupère les subordonnés directs uniquement (N+1)
     * Vérifie que l'employé est dans superieurs[0] (premier niveau)
     */
    @Query(value = """
        SELECT 
            e.id as employeId,
            e.matricule,
            e.nom,
            e.prenom,
            e.titre,
            e.sexe,
            e.telephone,
            e.email,
            e.date_naissance as dateNaissance,
            c.id as contratEmployeId,
            c.type_contrat as typeContrat,
            c.status_contrat as statusContrat,
            c.date_embauche as dateEmbauche,
            c.salaire_base as salaireBase,
            c.salaire_brut as salaireBrut,
            p.id as posteId,
            p.libelle as posteLibelle,
            d.id as departementId,
            d.libelle as departementLibelle,
            cat.name as categorieEmploye,
            comp.id as companyId,
            comp.name as companyName,
            es.is_active as isActive,
            es.created_at as createdAt,
            es.updated_at as updatedAt
        FROM employe_superieur es
        JOIN employes e ON e.id = es.employe_id
        JOIN contrat_employes c ON c.id = es.contrat_employe_id
        JOIN companies comp ON comp.id = es.company_id
        LEFT JOIN postes p ON p.id = c.poste_id
        LEFT JOIN departements d ON d.id = c.departement_id
        LEFT JOIN category_employes cat ON cat.id = c.category_employe_id
        WHERE es.is_active = true
        AND JSON_EXTRACT(es.hierarchie, '$.superieurs[0].employe_superieur_id') = :superieurId
        ORDER BY e.nom ASC, e.prenom ASC
    """, nativeQuery = true)
    List<Object[]> findSubordonnesDirectsBySuperieurId(@Param("superieurId") Long superieurId);

    /**
     * Compte le nombre de subordonnés
     */
    @Query(value = """
        SELECT COUNT(DISTINCT e.id)
        FROM employe_superieur es
        JOIN employes e ON e.id = es.employe_id
        WHERE es.is_active = true
        AND e.id != :superieurId
        AND JSON_SEARCH(
            es.hierarchie,
            'one',
            CAST(:superieurId AS CHAR),
            NULL,
            '$.superieurs[*].employe_superieur_id'
        ) IS NOT NULL
    """, nativeQuery = true)
    Long countSubordonnesBySuperieurId(@Param("superieurId") Long superieurId);

    /**
     * VERSION ALTERNATIVE avec JSON_CONTAINS (peut être plus performant)
     */
    @Query(value = """
        SELECT 
            e.id, e.matricule, e.nom, e.prenom, e.titre, e.sexe, 
            e.telephone, e.email, e.date_naissance,
            c.id, c.type_contrat, c.status_contrat, c.date_embauche,
            c.salaire_base, c.salaire_brut,
            p.id, p.libelle, d.id, d.libelle, cat.name,
            comp.id, comp.name, es.is_active, es.created_at, es.updated_at
        FROM employe_superieur es
        JOIN employes e ON e.id = es.employe_id
        JOIN contrat_employes c ON c.id = es.contrat_employe_id
        JOIN companies comp ON comp.id = es.company_id
        LEFT JOIN postes p ON p.id = c.poste_id
        LEFT JOIN departements d ON d.id = c.departement_id
        LEFT JOIN category_employes cat ON cat.id = c.category_employe_id
        WHERE es.is_active = true
        AND e.id != :superieurId
        AND JSON_CONTAINS(
            JSON_EXTRACT(es.hierarchie, '$.superieurs[*].employe_superieur_id'),
            CAST(:superieurId AS JSON)
        )
        ORDER BY e.nom ASC, e.prenom ASC
    """, nativeQuery = true)
    List<Object[]> findSubordonnesBySuperieurIdV2(@Param("superieurId") Long superieurId);

    /**
     * DEBUG : Récupérer toutes les hiérarchies pour voir le contenu
     */
    @Query(value = """
        SELECT 
            es.id,
            e.id as employe_id,
            e.nom,
            e.prenom,
            es.hierarchie
        FROM employe_superieur es
        JOIN employes e ON e.id = es.employe_id
        WHERE es.is_active = true
        ORDER BY e.nom ASC
    """, nativeQuery = true)
    List<Object[]> findAllHierarchiesForDebug();
}