package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ModeDePaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeDePaiementRepository extends JpaRepository<ModeDePaiement, Long> {
    boolean existsByLibelle(String libelle);
    Optional<ModeDePaiement> findByLibelle(String libelle);
}
