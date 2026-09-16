package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.TypeAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeAbsenceRepository extends JpaRepository<TypeAbsence, Long> {
    // Si tu veux des méthodes spécifiques, on pourra les ajouter ici
    Optional<TypeAbsence> findByLibelle(String libelle);

    boolean existsByLibelle(String libelle);
}
