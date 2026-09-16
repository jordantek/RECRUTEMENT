package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.NatureContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NatureContratRepository extends JpaRepository<NatureContrat, Long> {
    boolean existsByLibelle(String libelle);
    Optional<NatureContrat> findByLibelle(String libelle);
}
