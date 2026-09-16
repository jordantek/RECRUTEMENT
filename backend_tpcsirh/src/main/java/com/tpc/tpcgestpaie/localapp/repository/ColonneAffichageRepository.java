package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ColonneAffichage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ColonneAffichageRepository extends JpaRepository<ColonneAffichage, Long> {
    boolean existsByLibelle(String libelle);
    Optional<ColonneAffichage> findByLibelle(String libelle);
//    Optional<ColonneAffichage> findByStartLibelle(String libelle);
}
