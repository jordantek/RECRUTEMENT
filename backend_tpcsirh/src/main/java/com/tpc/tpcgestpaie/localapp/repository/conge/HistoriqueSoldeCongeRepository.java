package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.HistoriqueSoldeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueSoldeCongeRepository extends JpaRepository<HistoriqueSoldeConge, Long> {
    List<HistoriqueSoldeConge> findByEmployeIdOrderByDateEvenementDesc(Long employeId);
    List<HistoriqueSoldeConge> findByEmployeIdAndTypeEvenementOrderByDateEvenementDesc(
            Long employeId,
            HistoriqueSoldeConge.TypeEvenementSolde typeEvenement
    );

    boolean existsByEmployeId(Long employeId);
}