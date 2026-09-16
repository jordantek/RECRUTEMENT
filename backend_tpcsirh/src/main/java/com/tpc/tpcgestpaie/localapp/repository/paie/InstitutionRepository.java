package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    boolean existsByName(String name);
    Optional<Institution> findByName(String name);
}