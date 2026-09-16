package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentSubCategoryRepository extends JpaRepository<DocumentSubCategory, Long> {
    List<DocumentSubCategory> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);

    // ⚠️ CORRECTION : Utiliser la relation correcte
    @Query("SELECT sc FROM DocumentSubCategory sc WHERE sc.isMandatory = true " +
            "AND sc.category.company.id = :companyId")
    List<DocumentSubCategory> findByIsMandatoryTrueAndCategoryCompanyId(@Param("companyId") Long companyId);

    Optional<DocumentSubCategory> findByCategoryCompanyIdAndId(Long companyId, Long id);

    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
}