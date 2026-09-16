package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.SoldesConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoldeCongeRepository extends JpaRepository<SoldesConge, Long> {
    Optional<SoldesConge> findByEmployeId(Long employeId);
    boolean existsByEmployeId(Long employeId);
}