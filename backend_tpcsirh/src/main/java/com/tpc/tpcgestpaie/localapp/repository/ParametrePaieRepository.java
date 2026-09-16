package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ParametrePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametrePaieRepository extends JpaRepository<ParametrePaie, Long> {

    Optional<ParametrePaie> findByUniqueKey(String uniqueKey);

    boolean existsByUniqueKey(String uniqueKey);

}
