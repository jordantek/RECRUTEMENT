package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.LienParente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LienParenteRepository extends JpaRepository<LienParente, Long> {
    boolean existsByLibelle(String libelle);
}