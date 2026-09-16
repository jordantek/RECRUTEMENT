package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    // === MÉTHODES PAGINÉES ===

    @Query("SELECT f FROM Formation f " +
            "LEFT JOIN FETCH f.company " +
            "LEFT JOIN FETCH f.employes")
    Page<Formation> findAllWithAssociations(Pageable pageable);

    @Query("SELECT f FROM Formation f " +
            "LEFT JOIN FETCH f.company " +
            "LEFT JOIN FETCH f.employes " +
            "WHERE f.lieu = :lieu")
    Page<Formation> findByLieu(@Param("lieu") String lieu, Pageable pageable);

    @Query("SELECT f FROM Formation f LEFT JOIN FETCH f.employes WHERE f.id = :id")
    Optional<Formation> findByIdWithEmployes(@Param("id") Long id);

    @Query("SELECT f FROM Formation f " +
            "LEFT JOIN FETCH f.company " +
            "LEFT JOIN FETCH f.employes " +
            "WHERE LOWER(f.theme) LIKE LOWER(CONCAT('%', :theme, '%'))")
    Page<Formation> findByTheme(@Param("theme") String theme, Pageable pageable);

    @Query("SELECT f FROM Formation f " +
            "LEFT JOIN FETCH f.company " +
            "LEFT JOIN FETCH f.employes " +
            "WHERE f.company.id = :companyId")
    Page<Formation> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT f FROM Formation f " +
            "LEFT JOIN FETCH f.company " +
            "LEFT JOIN FETCH f.employes e " +
            "WHERE e.id = :employeId")
    Page<Formation> findByEmployeId(@Param("employeId") Long employeId, Pageable pageable);

    @Query("SELECT COUNT(f) > 0 FROM Formation f WHERE f.company.id = :companyId AND f.theme = :theme AND (:formationId IS NULL OR f.id <> :formationId)")
    boolean existsByCompanyIdAndTheme(
            @Param("companyId") Long companyId,
            @Param("theme") String theme,
            @Param("formationId") Long formationId
    );

    // Méthodes de comptage pour la pagination
    @Query("SELECT COUNT(f) FROM Formation f WHERE f.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(f) FROM Formation f JOIN f.employes e WHERE e.id = :employeId")
    long countByEmployeId(@Param("employeId") Long employeId);
}