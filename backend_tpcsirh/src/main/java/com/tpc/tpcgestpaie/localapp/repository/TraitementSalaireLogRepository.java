package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.TraitementSalaireLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraitementSalaireLogRepository extends JpaRepository<TraitementSalaireLog, Long> {

    Optional<TraitementSalaireLog> findById(Long id);
    List<TraitementSalaireLog> findByCompanyId(Long companyId);
    List<TraitementSalaireLog> findByCompanyIdOrderByDateTraitementDesc(Long companyId);
    Page<TraitementSalaireLog> findByCompanyIdOrderByDateTraitementDesc(Long companyId, Pageable pageable);

    List<TraitementSalaireLog> findByCompanyIdAndMois(Long companyId, String mois);
    List<TraitementSalaireLog> findByStatut(String statut);

    boolean existsByCompanyIdAndMois(Long id, String mois);

    void deleteByCompanyIdAndMois(Long id, String mois);
}
