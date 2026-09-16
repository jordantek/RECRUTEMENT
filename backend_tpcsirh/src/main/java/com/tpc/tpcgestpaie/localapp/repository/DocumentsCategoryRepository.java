package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.DocumentsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentsCategoryRepository extends JpaRepository<DocumentsCategory, Long> {

    Optional<DocumentsCategory> findByCodeAndCompanyId(String code, Long companyId);

    boolean existsByCodeAndCompanyId(String code, Long companyId);

    List<DocumentsCategory> findByCompanyId(Long companyId);

    List<DocumentsCategory> findByCompanyIdIsNull();

    // catégories globales + catégories de l'entreprise
    List<DocumentsCategory> findByCompanyIdOrCompanyIdIsNull(Long companyId);
    boolean existsByCode(String code);

}