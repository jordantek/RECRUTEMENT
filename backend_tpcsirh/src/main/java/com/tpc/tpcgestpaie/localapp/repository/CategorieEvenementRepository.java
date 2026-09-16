package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.CategorieEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieEvenementRepository extends JpaRepository<CategorieEvenement, Long> {
    boolean existsByLibelle(String libelle);

    List<CategorieEvenement> findAllByDeletedAtIsNull();


}
