package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.NiveauAffichage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NiveauAffichageRepository extends JpaRepository<NiveauAffichage, Long> {
    boolean existsByLibelle(String libelle);
    Optional<NiveauAffichage> findByLibelle(String libelle);
//    Optional<NiveauAffichage> findByStartLibelle(String libelle);
}
