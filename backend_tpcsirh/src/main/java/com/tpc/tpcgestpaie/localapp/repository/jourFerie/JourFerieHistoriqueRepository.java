package com.tpc.tpcgestpaie.localapp.repository.jourFerie;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité JourFerieHistorique
 */
@Repository
public interface JourFerieHistoriqueRepository extends JpaRepository<JourFerieHistorique, Long> {

    /**
     * Trouve l'historique d'une entreprise
     */
    List<JourFerieHistorique> findByCompanyOrderByDateModificationDesc(Company company);

    /**
     * Trouve l'historique d'une association jour férié-entreprise
     */
    List<JourFerieHistorique> findByJourFerieEntrepriseOrderByDateModificationDesc(
            JourFerieEntreprise jourFerieEntreprise
    );

    /**
     * Trouve l'historique par type d'action
     */
    List<JourFerieHistorique> findByCompanyAndTypeActionOrderByDateModificationDesc(
            Company company, JourFerieHistorique.TypeActionJourFerie typeAction
    );

    /**
     * Trouve l'historique entre deux dates
     */
    List<JourFerieHistorique> findByCompanyAndDateModificationBetweenOrderByDateModificationDesc(
            Company company, LocalDateTime dateDebut, LocalDateTime dateFin
    );

    /**
     * Trouve l'historique d'un utilisateur
     */
    List<JourFerieHistorique> findByModifiedByOrderByDateModificationDesc(Long userId);

    /**
     * Trouve les dernières modifications d'une entreprise
     */
    @Query("SELECT jfh FROM JourFerieHistorique jfh " +
            "WHERE jfh.company = :company " +
            "ORDER BY jfh.dateModification DESC")
    List<JourFerieHistorique> findDernieresModifications(
            @Param("company") Company company,
            @Param("limit") int limit
    );

    /**
     * Compte le nombre de modifications par type d'action
     */
    long countByCompanyAndTypeAction(Company company, JourFerieHistorique.TypeActionJourFerie typeAction);
}