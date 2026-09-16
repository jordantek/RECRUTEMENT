package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

    // Récupère les catégories de l'entreprise + les catégories globales (company null)
    @Query("SELECT dc FROM DocumentCategory dc WHERE dc.company.id = :companyId OR dc.company IS NULL ORDER BY dc.displayOrder ASC")
    List<DocumentCategory> findByCompanyIdOrderByDisplayOrderAsc(@Param("companyId") Long companyId);

    @Query("SELECT dc FROM DocumentCategory dc WHERE dc.company.id = :companyId " +
            "AND LOWER(dc.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<DocumentCategory> searchInCompany(@Param("companyId") Long companyId,
                                           @Param("searchTerm") String searchTerm);

    // Pour vérifier l'existence avec company
    boolean existsByCompanyAndName(Company company, String name);

    // Pour vérifier l'existence sans company
    boolean existsByNameAndCompanyIsNull(String name);

    boolean existsByCompanyIdAndNameIgnoreCase(Long companyId, String name);

    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(Long companyId, String name, Long id);


    // Toutes les catégories d'un client
    List<DocumentCategory> findByClient(Company client);

    // Catégories partagées d'un client
    List<DocumentCategory> findByClientAndCompanyIsNull(Company client);

    // Catégories spécifiques à une entreprise
    List<DocumentCategory> findByCompany(Company company);

    // Catégories accessibles pour une entreprise
    @Query("SELECT dc FROM DocumentCategory dc WHERE " +
            "(dc.client = :client AND dc.company IS NULL) OR " + // Catégories partagées du client
            "dc.company = :company") // Catégories spécifiques à l'entreprise
    List<DocumentCategory> findAccessibleCategories(
            @Param("client") Company client,
            @Param("company") Company company
    );


    // Vérifier l'existence par client (remplace l'ancienne méthode)
    boolean existsByClientAndNameIgnoreCase(Company client, String name);

    // Vérifier l'existence par client en excluant un ID
    boolean existsByClientAndNameIgnoreCaseAndIdNot(Company client, String name, Long id);

    // Catégories accessibles pour une entreprise
    @Query("SELECT dc FROM DocumentCategory dc WHERE " +
            "((dc.client.id = :clientId AND dc.company IS NULL) OR " + // Catégories partagées
            "dc.company.id = :companyId) " + // Catégories spécifiques
            "ORDER BY dc.displayOrder ASC")
    List<DocumentCategory> findAccessibleCategories(@Param("clientId") Long clientId,
                                                    @Param("companyId") Long companyId);

    // Version simplifiée
    @Query("SELECT dc FROM DocumentCategory dc WHERE " +
            "((dc.client.id IN (SELECT c.client.id FROM Company c WHERE c.id = :companyId) AND dc.company IS NULL) OR " +
            "dc.company.id = :companyId) " +
            "ORDER BY dc.displayOrder ASC")
    List<DocumentCategory> findAccessibleCategoriesByCompany(@Param("companyId") Long companyId);

    // Vérifier l'accès à une catégorie
    @Query("SELECT COUNT(dc) > 0 FROM DocumentCategory dc WHERE " +
            "dc.id = :categoryId AND " +
            "((dc.client.id = :clientId AND dc.company IS NULL) OR " +
            "dc.company.id = :companyId)")
    boolean hasAccessToCategory(@Param("categoryId") Long categoryId,
                                @Param("clientId") Long clientId,
                                @Param("companyId") Long companyId);


    boolean existsByClientAndCompanyAndNameIgnoreCase(Company client, Company company, String name);
    boolean existsByClientAndCompanyAndNameIgnoreCaseAndIdNot(Company client, Company company, String name, Long id);
}