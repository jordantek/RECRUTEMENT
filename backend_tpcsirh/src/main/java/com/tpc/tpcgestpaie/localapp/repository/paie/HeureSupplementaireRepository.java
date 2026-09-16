package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.HeureSupplementaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeureSupplementaireRepository extends JpaRepository<HeureSupplementaire, Long> {

    Optional<HeureSupplementaire> findByEmployeIdAndMois(Long employeId, String mois);

    List<HeureSupplementaire> findAllByCompany_Id(Long entrepriseId);

    @Query("SELECT h FROM HeureSupplementaire h " +
            "WHERE h.company.id = :entrepriseId " +
            "AND h.mois = :mois")
    List<HeureSupplementaire> findAllByCompanyIdAndMois(@Param("entrepriseId") Long entrepriseId,
                                                        @Param("mois") String mois);

    // Ici tu peux ajouter des méthodes personnalisées si besoin, par exemple :

    // List<HeureSupplementaire> findByEmployeId(Long employeId);
    // List<HeureSupplementaire> findByContratEmployeIdAndMois(Long contratEmployeId, String mois);
}
