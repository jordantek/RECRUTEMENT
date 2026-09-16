package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    boolean existsByLibelle(String libelle);

    boolean existsByLibelleAndCompanyId(String libelle, Long companyId);
    List<Departement> findByCompanyId(Long companyId);

    boolean existsByIdAndCompanyId(Long id, Long companyId);

}
