package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentTemplate;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {
    List<DocumentTemplate> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId);
    Optional<DocumentTemplate> findBySubCategoryId(Long subCategoryId);

    Optional<DocumentTemplate> findBySubCategory(DocumentSubCategory subCategory);

    List<DocumentTemplate> findByIsActiveTrue();

}