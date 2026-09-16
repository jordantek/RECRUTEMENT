package com.tpc.tpcgestpaie.localapp.repository.jourtravail;

import com.tpc.tpcgestpaie.localapp.model.jourtravail.JoursTravailles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JoursTravaillesRepository extends JpaRepository<JoursTravailles, Long> {
    Optional<JoursTravailles> findByCompanyId(Long companyId);
    boolean existsByCompanyId(Long companyId);
}