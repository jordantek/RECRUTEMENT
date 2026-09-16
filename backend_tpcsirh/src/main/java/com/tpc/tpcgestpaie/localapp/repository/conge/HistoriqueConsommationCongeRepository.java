package com.tpc.tpcgestpaie.localapp.repository.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.HistoriqueConsommationConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueConsommationCongeRepository extends JpaRepository<HistoriqueConsommationConge, Long> {

    List<HistoriqueConsommationConge> findByEmployeIdOrderByDateConsommationDesc(Long employeId);

    List<HistoriqueConsommationConge> findByDemandeAbsenceId(Long demandeAbsenceId);

    List<HistoriqueConsommationConge> findByEmployeIdAndProvisionAnneeMois(Long employeId, String anneeMois);
}