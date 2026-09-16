package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.CategorieEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieEmployeRepository extends JpaRepository<CategorieEmploye, Long> {
    boolean existsByName(String name);
}
