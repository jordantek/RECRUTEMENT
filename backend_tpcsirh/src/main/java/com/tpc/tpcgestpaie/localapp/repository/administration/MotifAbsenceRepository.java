package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.MotifAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotifAbsenceRepository extends JpaRepository<MotifAbsence, Long> {
    Optional<MotifAbsence> findByLibelle(String libelle);
    boolean existsByLibelle(String libelle);
}
