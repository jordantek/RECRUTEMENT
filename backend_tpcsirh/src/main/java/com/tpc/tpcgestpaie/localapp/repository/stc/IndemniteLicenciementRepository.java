package com.tpc.tpcgestpaie.localapp.repository.stc;

import com.tpc.tpcgestpaie.localapp.model.IndemniteLicenciement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndemniteLicenciementRepository extends JpaRepository<IndemniteLicenciement, Long> {
    // Tu peux ajouter ici des méthodes de recherche personnalisées si besoin

    boolean existsByContratEmployeIdAndMoisCalculSalaireAndTypeLicencement(Long contratId, YearMonth mois, String typeLicencement);

    List<IndemniteLicenciement> findByEmployeIdAndCompanyId(Long employeId, Long companyId);

    List<IndemniteLicenciement> findByEmployeId(Long employeId);

    List<IndemniteLicenciement> findByCompanyId(Long companyId);

    // Indemnité selon ancienneté
    @Query("SELECT i.indemniteSelonAnciennete FROM IndemniteLicenciement i WHERE i.contratEmploye.id = :contratEmployeId AND i.moisCalculSalaire = :mois")
    Optional<BigDecimal> findMontantByContratEmployeIdAndMois(@Param("contratEmployeId") Long contratEmployeId,
                                                              @Param("mois") YearMonth mois);

    // Montant moyen licenciement (à calculer dans la requête)
    @Query("SELECT AVG(i.montantMoyen) FROM IndemniteLicenciement i WHERE i.contratEmploye.id = :contratEmployeId AND i.moisCalculSalaire = :mois")
    Optional<BigDecimal> findMontantMoyenByContratEmployeIdAndMois(@Param("contratEmployeId") Long contratEmployeId,
                                                                   @Param("mois") YearMonth mois);
    @Query("SELECT i FROM IndemniteLicenciement i WHERE i.contratEmploye.id = :contratEmployeId AND i.moisCalculSalaire = :mois")
    Optional<IndemniteLicenciement> findByContratEmployeIdAndMois(@Param("contratEmployeId") Long contratEmployeId,
                                                                  @Param("mois") YearMonth mois);



}
