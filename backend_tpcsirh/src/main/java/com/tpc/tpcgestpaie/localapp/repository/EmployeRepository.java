package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    @Query("SELECT e FROM Employe e JOIN FETCH e.company WHERE e.company.id = :companyId")
    List<Employe> findByCompanyIdWithCompany(@Param("companyId") Long companyId);

    // === MÉTHODES DE VÉRIFICATION ===
    boolean existsByMatricule(String matricule);
    boolean existsByNumeroIfu(String numero_ifu);
    boolean existsByNumeroCnss(String numeroCnss);

    // === MÉTHODES PAGINÉES ===

    // Tous les employés avec pagination (déjà fourni par JpaRepository)
    // Page<Employe> findAll(Pageable pageable); // Hérité de JpaRepository

    // Employés sans contrat avec pagination
//    @Query("SELECT e FROM Employe e WHERE e.id NOT IN (SELECT c.employe.id FROM ContratEmploye c)")
//    Page<Employe> findAllEmployesSansContrat(Pageable pageable);
//

    Optional<Employe> findByMatricule(String matricule);
    // === CORRECTION : Employés sans contrat avec JOIN FETCH ===
    @Query("SELECT DISTINCT e FROM Employe e " +
            "LEFT JOIN FETCH e.company c " +
            "LEFT JOIN FETCH c.managedCompanies " +
            "LEFT JOIN FETCH e.added_by " +
            "LEFT JOIN FETCH e.updated_by " +
            "LEFT JOIN FETCH e.formations " +
            "WHERE e.id NOT IN (SELECT c.employe.id FROM ContratEmploye c)")
    Page<Employe> findAllEmployesSansContrat(Pageable pageable);
    // Employés récents avec pagination
    @Query("SELECT e FROM Employe e ORDER BY e.created_at DESC")
    Page<Employe> findAllByOrderByCreated_atDesc(Pageable pageable);

    // Employés par entreprise avec pagination (contrats non terminés)
    // Employés par entreprise avec pagination - APPROCHE ALTERNATIVE
    @Query("SELECT e FROM Employe e WHERE e.id IN (SELECT c.employe.id FROM ContratEmploye c WHERE c.company.id = :companyId AND c.status_contrat != 'TERMINE')")
    Page<Employe> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT e FROM Employe e WHERE e.id IN (SELECT c.employe.id FROM ContratEmploye c WHERE c.company.id = :companyId)")
    Page<Employe> findAllByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    //liste des employés par entreprise pour numérisation

    @Query("SELECT DISTINCT e FROM Employe e " +
            "LEFT JOIN FETCH e.company c " +
//            "LEFT JOIN FETCH c.managedCompanies " +
//            "LEFT JOIN FETCH e.added_by " +
//            "LEFT JOIN FETCH e.updated_by " +
//            "LEFT JOIN FETCH e.formations " +
            "WHERE e.company.id= :companyId")
//    @Query("SELECT e FROM Employe e WHERE e.company.id= :companyId")
    Page<Employe> findAllByCompany(@Param("companyId") Long companyId, Pageable pageable);

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===
    @Query(value = "SELECT * FROM employes e WHERE e.id NOT IN (SELECT employe_id FROM contrat_employes)", nativeQuery = true)
    List<Employe> findAllEmployesSansContrat();

    @Query(value = "SELECT * FROM employes ORDER BY created_at DESC", nativeQuery = true)
    List<Employe> findAllByOrderByCreated_atDesc();

    @Query("SELECT DISTINCT c.employe FROM ContratEmploye c WHERE c.company.id = :companyId AND c.status_contrat != 'TERMINE'")
    List<Employe> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT DISTINCT c.employe FROM ContratEmploye c WHERE c.company.id = :companyId AND c.arretContrat = false ")
    List<Employe> findAllByCompanyId(@Param("companyId") Long companyId);

    // === MÉTHODES DE RECHERCHE AVANCÉE PAGINÉES (optionnelles) ===

    // Recherche d'employés par nom avec pagination
    @Query("SELECT e FROM Employe e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Employe> findByNomContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Recherche d'employés par matricule avec pagination
    @Query("SELECT e FROM Employe e WHERE e.matricule LIKE CONCAT('%', :matricule, '%')")
    Page<Employe> findByMatriculeContaining(@Param("matricule") String matricule, Pageable pageable);

    // Employés par entreprise et département avec pagination
    @Query("SELECT DISTINCT c.employe FROM ContratEmploye c WHERE c.company.id = :companyId AND c.departement.id = :departementId AND c.status_contrat != 'TERMINE'")
    Page<Employe> findByCompanyAndDepartement(@Param("companyId") Long companyId, @Param("departementId") Long departementId, Pageable pageable);

    // Nouvelle méthode avec JOIN FETCH pour éviter Lazy Loading
    @Query("SELECT e FROM Employe e LEFT JOIN FETCH e.company LEFT JOIN FETCH e.added_by LEFT JOIN FETCH e.updated_by WHERE e.company.id = :companyId")
    List<Employe> findAllByCompanyWithFetch(@Param("companyId") Long companyId);

    // Version paginée avec fetch
    @Query("SELECT e FROM Employe e LEFT JOIN FETCH e.company WHERE e.company.id = :companyId")
    Page<Employe> findAllByCompanyWithFetch(@Param("companyId") Long companyId, Pageable pageable);

    // Solution avec sous-requête pour éviter le problème DISTINCT
    @Query("SELECT e FROM Employe e " +
            "LEFT JOIN FETCH e.company " +
            "LEFT JOIN FETCH e.added_by " +
            "LEFT JOIN FETCH e.updated_by " +
            "LEFT JOIN FETCH e.formations " +
            "WHERE e.id IN (" +
            "   SELECT e2.id FROM Employe e2 WHERE e2.company.id = :companyId" +
            ")")
    Page<Employe> findAllByCompanyWithAllFetch(@Param("companyId") Long companyId, Pageable pageable);

    // Solution avec sous-requête pour éviter le problème DISTINCT
    @Query("SELECT e FROM Employe e " +
            "LEFT JOIN FETCH e.company " +
            "WHERE e.id IN (" +
            "   SELECT e2.id FROM Employe e2 WHERE e2.company.id = :companyId" +
            ")" +
            " AND (" +
            "        LOWER(e.nom) LIKE :keyword" +
            "        OR LOWER(e.prenom) LIKE :keyword" +
            "        OR LOWER(e.matricule) LIKE :keyword" +
            "    )" +
            "")
    Page<Employe> searchByCompanyWithAllFetch(@Param("companyId") Long companyId, @Param("keyword") String keyword, Pageable pageable);


    /**
     * Récupère les employés des entreprises accessibles par l'utilisateur via leurs contrats actifs
     */
//    @Query("SELECT DISTINCT e FROM Employe e " +
//            "JOIN e.company c " +
//            "WHERE e.id IN (" +
//            "   SELECT ce.employe.id FROM ContratEmploye ce " +
//            "   WHERE ce.company.id IN :companyIds AND ce.arretContrat = false" +
//            ") AND e.deleted_at IS NULL")
//    List<Employe> findByCompanyIds(@Param("companyIds") List<Long> companyIds);
//

    /**
     * Récupère les employés avec pagination via leurs contrats actifs
     */
    @Query("SELECT DISTINCT c.employe FROM ContratEmploye c WHERE c.company.id IN :companyIds AND c.arretContrat = false AND c.employe.deleted_at IS NULL")
    Page<Employe> findByCompanyIds(@Param("companyIds") List<Long> companyIds, Pageable pageable);

    /**
     * Récupère les anniversaires prochains pour les entreprises accessibles via contrats actifs
     */
    @Query("SELECT DISTINCT c.employe FROM ContratEmploye c WHERE c.company.id IN :companyIds AND c.arretContrat = false AND c.employe.deleted_at IS NULL " +
            "AND FUNCTION('MONTH', c.employe.date_naissance) = :month AND FUNCTION('DAY', c.employe.date_naissance) BETWEEN :startDay AND :endDay")
    List<Employe> findUpcomingBirthdays(@Param("companyIds") List<Long> companyIds,
                                        @Param("month") int month,
                                        @Param("startDay") int startDay,
                                        @Param("endDay") int endDay);

    /**
     * Compte le nombre d'employés par entreprise accessible via contrats actifs
     */
    @Query("SELECT c.company.id, COUNT(DISTINCT c.employe) FROM ContratEmploye c WHERE c.company.id IN :companyIds AND c.arretContrat = false AND c.employe.deleted_at IS NULL GROUP BY c.company.id")
    List<Object[]> countByAccessibleCompanies(@Param("companyIds") List<Long> companyIds);


    /**
     * Vérifie si un employé a un contrat actif avec une entreprise
     */
    @Query("SELECT COUNT(c) > 0 FROM ContratEmploye c WHERE c.employe.id = :employeId AND c.company.id = :companyId AND c.arretContrat = false")
    boolean hasActiveContractWithCompany(@Param("employeId") Long employeId, @Param("companyId") Long companyId);

    /**
     * Récupère les entreprises d'un employé via ses contrats actifs
     */
    @Query("SELECT DISTINCT ce.company FROM ContratEmploye ce " +
            "WHERE ce.employe.id = :employeId AND ce.arretContrat = false")
    List<Company> findCompaniesByEmployeId(@Param("employeId") Long employeId);

    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT DISTINCT e FROM Employe e " +
            "JOIN FETCH e.company " + // ← JOIN FETCH pour charger la company
            "JOIN ContratEmploye ce ON ce.employe.id = e.id " +
            "WHERE ce.company.id IN :companyIds AND ce.arretContrat = false AND e.deleted_at IS NULL")
    List<Employe> findByCompanyIds(@Param("companyIds") List<Long> companyIds);


    // 🔥 Employés sans compte utilisateur par entreprise
    @Query("SELECT e FROM Employe e WHERE e.company.id = :companyId " +
            "AND e.deleted_at IS NULL " +
            "AND NOT EXISTS (SELECT u FROM User u WHERE u.employe.id = e.id AND u.deletedAt IS NULL) " +
            "ORDER BY e.nom, e.prenom")
    List<Employe> findEmployesSansCompteByCompany(@Param("companyId") Long companyId);

    // 🔥 NOUVEAU : Liste des entreprises qui ont des employés sans compte
    @Query("SELECT DISTINCT e.company.id, e.company.name FROM Employe e " +
            "WHERE e.deleted_at IS NULL " +
            "AND NOT EXISTS (SELECT u FROM User u WHERE u.employe.id = e.id AND u.deletedAt IS NULL) " +
            "ORDER BY e.company.name")
    List<Object[]> findCompaniesWithEmployesSansCompte();

    // Vérifier si un employé a déjà un compte
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.employe.id = :employeId AND u.deletedAt IS NULL")
    boolean employeHasUserAccount(@Param("employeId") Long employeId);

}