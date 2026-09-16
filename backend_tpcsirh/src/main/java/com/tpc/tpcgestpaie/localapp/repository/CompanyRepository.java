package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ActivityArea;
import com.tpc.tpcgestpaie.localapp.model.Banque;
import com.tpc.tpcgestpaie.localapp.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByEmail(String email);
    boolean existsByNss(String nss);
    boolean existsByRccm(String rccm);
    boolean existsByIfu(String ifu);
    List<Company> findAllByOrderByNameAsc();

    // Chiffre d'affaires d'une entreprise
    @Query("SELECT c.ca FROM Company c WHERE c.id = :companyId")
    BigDecimal findChiffreAffairesById(@Param("companyId") Long companyId);

    // Liste des entreprises NON affectées à un utilisateur
    @Query("SELECT c FROM Company c WHERE c.id NOT IN (" +
            "SELECT uc.company.id FROM UserCompany uc WHERE uc.removedAt IS NULL)")
    List<Company> findCompaniesNotAssigned();


    @Query("SELECT DISTINCT c FROM Company c " +

            "LEFT JOIN FETCH c.activityAreas " +
            "WHERE c.id = :id")
    Optional<Company> findByIdWithAssociations(@Param("id") Long id);

    // Requête native pour récupérer les ActivityArea d'une company
    @Query(value = "SELECT aa.* FROM activity_areas aa " +
            "INNER JOIN company_activity_areas caa ON aa.id = caa.activity_area_id " +
            "WHERE caa.company_id = :companyId",
            nativeQuery = true)
    List<ActivityArea> findActivityAreasByCompanyId(@Param("companyId") Long companyId);

    // Requête native pour récupérer les Banques d'une company
    @Query(value = "SELECT b.* FROM banques b " +
            "INNER JOIN company_banques cb ON b.id = cb.banque_id " +
            "WHERE cb.company_id = :companyId",
            nativeQuery = true)
    List<Banque> findBanquesByCompanyId(@Param("companyId") Long companyId);

    // Entreprises gérées par un client
    List<Company> findByClientId(Long clientId);

    // Clients (entreprises sans client parent)
    List<Company> findByClientIsNull();

    // Entreprises disponibles pour liaison
    List<Company> findByClientIsNullAndIdNot(Long excludedCompanyId);

    // Trouver le client d'une entreprise
    @Query("SELECT c.client FROM Company c WHERE c.id = :companyId")
    Optional<Company> findClientByCompanyId(@Param("companyId") Long companyId);

    // Vérifier si une entreprise est un client
    @Query("SELECT COUNT(c) > 0 FROM Company c WHERE c.id = :companyId AND c.client IS NULL")
    boolean isClientCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.client.id = :clientId")
    int countManagedCompaniesByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COUNT(c) FROM Company c WHERE c.client IS NULL")
    long countByClientIsNull();

    boolean existsByName(String name);

    // Ajoutez cette méthode
    Company findByRccm(String rccm);

    // Ou si vous préférez avec Optional
    Optional<Company> findOptionalByRccm(String rccm);

}