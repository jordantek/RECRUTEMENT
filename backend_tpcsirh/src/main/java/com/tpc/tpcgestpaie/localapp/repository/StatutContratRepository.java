package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.StatutContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatutContratRepository extends JpaRepository<StatutContrat, Long> {

    // Trouver le statut d'initialisation (contrat de base)
    @Query("SELECT sc FROM StatutContrat sc " +
            "WHERE sc.contratEmploye.employe.id = :employeId " +
            "AND sc.initialisation = true")
    Optional<StatutContrat> findStatutInitialisationByEmployeId(@Param("employeId") Long employeId);

    // Trouver toutes les modifications entre deux dates

    // Vérifiez que cette méthode existe exactement comme ça
    @Query("SELECT sc FROM StatutContrat sc " +
            "WHERE sc.contratEmploye.employe.id = :employeId " +
            "AND sc.initialisation = false " +  // Modifications seulement (pas d'initialisation)
            "AND sc.dateEffet <= :dateFin " +   // Date effet <= date référence
            "AND sc.dateEffet > :dateDebut " +  // Date effet > date initiale (optionnel)
            "ORDER BY sc.dateEffet ASC")        // Par ordre chronologique
    List<StatutContrat> findModificationsByEmployeAndDate(@Param("employeId") Long employeId,
                                                          @Param("dateDebut") LocalDate dateDebut,
                                                          @Param("dateFin") LocalDate dateFin);

    // Méthode plus simple sans filtre de date début
    @Query("SELECT sc FROM StatutContrat sc " +
            "WHERE sc.contratEmploye.employe.id = :employeId " +
            "AND sc.initialisation = false " +
            "AND sc.dateEffet <= :dateReference " +
            "ORDER BY sc.dateEffet ASC")
    List<StatutContrat> findModificationsSimple(@Param("employeId") Long employeId,
                                                @Param("dateReference") LocalDate dateReference);

    // Trouver le statut actif pour un employé à une date spécifique
    @Query("SELECT sc FROM StatutContrat sc " +
            "LEFT JOIN FETCH sc.contratEmploye ce " +
            "LEFT JOIN FETCH ce.employe e " +
            "WHERE e.id = :employeId " +
            "AND sc.dateEffet <= :dateReference " +
            "ORDER BY sc.dateEffet DESC")
    Optional<StatutContrat> findStatutByEmployeAndDate(@Param("employeId") Long employeId,
                                                       @Param("dateReference") LocalDate dateReference);

    @Query("SELECT sc FROM StatutContrat sc " +
            "LEFT JOIN FETCH sc.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.employe " +
            "WHERE ce.employe.id = :employeId " +
            "ORDER BY sc.dateEffet DESC")
    List<StatutContrat> findByEmployeIdWithRelations(@Param("employeId") Long employeId);
    // PAR celle-ci avec JOIN FETCH :
    @Query("SELECT sc FROM StatutContrat sc " +
            "LEFT JOIN FETCH sc.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.employe " +
            "WHERE sc.contratEmploye.id = :contratEmployeId " +
            "ORDER BY sc.dateEffet DESC")
    List<StatutContrat> findByContratEmployeIdWithRelations(@Param("contratEmployeId") Long contratEmployeId);

    // Ajoutez aussi cette méthode pour les différences :
    @Query("SELECT sc FROM StatutContrat sc " +
            "LEFT JOIN FETCH sc.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.employe " +
            "WHERE sc.id = :statutId")
    Optional<StatutContrat> findByIdWithRelations(@Param("statutId") Long statutId);
    // Trouver l'historique par ID de contrat employé
    List<StatutContrat> findByContratEmployeIdOrderByDateEffetDesc(Long contratEmployeId);

    // Trouver l'historique par ID d'employé
    @Query("SELECT sc FROM StatutContrat sc WHERE sc.contratEmploye.employe.id = :employeId ORDER BY sc.dateEffet DESC")
    List<StatutContrat> findByEmployeIdOrderByDateEffetDesc(@Param("employeId") Long employeId);

    // Version avec jointures pour optimiser les requêtes
    @Query("SELECT sc FROM StatutContrat sc " +
            "LEFT JOIN FETCH sc.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "WHERE ce.employe.id = :employeId " +
            "ORDER BY sc.dateEffet DESC")
    List<StatutContrat> findHistoriqueCompletByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT s FROM StatutContrat s " +
            "WHERE (s.dateEffet <= :dateEffet) " +
            "AND s.actif = false")
    List<StatutContrat> findStatutsAActiver(@Param("dateEffet") LocalDate dateEffet);

    // 🔹 Tous les statuts dont la date d'effet est passée ou égale et qui ne sont pas encore actifs
    @Query("SELECT s FROM StatutContrat s WHERE s.dateEffet <= :dateEffet AND s.actif = false")
    List<StatutContrat> findByDateEffetLessThanEqualAndActifFalse(@Param("dateEffet") LocalDate dateEffet);

    // 🔹 Tous les statuts d'un contrat donné
    @Query("SELECT s FROM StatutContrat s WHERE s.contratEmploye.id = :contratId")
    List<StatutContrat> findByContratEmployeId(@Param("contratId") Long contratId);


    @Query("SELECT s FROM StatutContrat s " +
            "WHERE s.contratEmploye = :contrat AND s.actif = true " +
            "ORDER BY s.dateEffet DESC")
    Optional<StatutContrat> findDernierStatutActif(@Param("contrat") ContratEmploye contratEmploye);


    List<StatutContrat> findByContratEmploye_Employe_Id(Long employeId);


    // Trouver les statuts actifs d'un contrat
    List<StatutContrat> findByContratEmployeIdAndActifTrue(Long contratEmployeId);

    // Trouver le statut actuel d'un contrat
    @Query("SELECT sc FROM StatutContrat sc WHERE sc.contratEmploye.id = :contratEmployeId AND sc.actif = true ORDER BY sc.dateEffet DESC")
    Optional<StatutContrat> findStatutActuelByContratEmployeId(@Param("contratEmployeId") Long contratEmployeId);



}
