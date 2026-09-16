package com.tpc.tpcgestpaie.localapp.repository;


import com.tpc.tpcgestpaie.localapp.model.IndemniteConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface IndemniteCongeRepository extends JpaRepository<IndemniteConge, Long> {
    Optional<IndemniteConge> findByEmployeIdAndMoisCalculSalaire(Long employeId, YearMonth moisCalculSalaire);
    List<IndemniteConge> findByCompanyId(Long companyId);

    // Montant total indemnité congé
    @Query("SELECT c.montantTotal FROM IndemniteConge c WHERE c.contratEmploye.id = :contratId AND c.moisCalculSalaire = :mois")
    Optional<BigDecimal> findMontantByContratEmployeIdAndMois(@Param("contratId") Long contratId, @Param("mois") YearMonth mois);


}