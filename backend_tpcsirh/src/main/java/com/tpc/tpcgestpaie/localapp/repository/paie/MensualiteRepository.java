package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.Mensualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MensualiteRepository extends JpaRepository<Mensualite, Long> {

    @Query("SELECT m FROM Mensualite m " +
            "JOIN FETCH m.company " +
            "JOIN FETCH m.employe " +
            "JOIN FETCH m.contratEmploye ce " +
            "JOIN FETCH m.institution " +
            "JOIN FETCH m.lastUpdateUser "+
            "JOIN FETCH ce.departement"

            )
    List<Mensualite> findAll();

    @Query("SELECT m FROM Mensualite m " +
            "JOIN FETCH m.company " +
            "JOIN FETCH m.employe " +
            "JOIN FETCH m.contratEmploye ce " +
            "JOIN FETCH m.institution " +
            "JOIN FETCH m.lastUpdateUser " +
            "JOIN FETCH ce.departement " +   // <-- espace manquant ici !
            "WHERE m.id = :id")
    Optional<Mensualite> findById(@Param("id") Long id);


    // Si tu veux aussi par company id :
    @Query("SELECT m FROM Mensualite m " +
            "JOIN FETCH m.company " +
            "JOIN FETCH m.employe " +
            "JOIN FETCH m.contratEmploye ce " +
            "JOIN FETCH m.institution " +
            "JOIN FETCH m.lastUpdateUser " +
            "JOIN FETCH ce.departement " +
            "WHERE m.company.id = :companyId")
    List<Mensualite> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT m FROM Mensualite m " +
            "JOIN FETCH m.company " +
            "JOIN FETCH m.employe " +
            "JOIN FETCH m.contratEmploye ce " +
            "JOIN FETCH m.institution " +
            "JOIN FETCH m.lastUpdateUser " +
            "JOIN FETCH ce.departement " +
            "WHERE m.employe.id = :employeId")
    List<Mensualite> findByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT m FROM Mensualite m " +
            "JOIN FETCH m.company " +
            "JOIN FETCH m.employe " +
            "JOIN FETCH m.contratEmploye ce " +
            "JOIN FETCH m.institution " +
            "JOIN FETCH m.lastUpdateUser " +
            "JOIN FETCH ce.departement " +
            "WHERE m.company.id = :companyId " +
            "AND m.moisDemarrage <= :mois " +
            "AND m.moisFin >= :mois " +
            "AND m.statut <> 'Soldée'")
    List<Mensualite> findByContratEmployeIdAndCompanyIdAndMoisAndStatutNotSoldee(
            @Param("companyId") Long companyId,
            @Param("mois") String mois);

    List<Mensualite> findByMoisDemarrageBetween(String moisDebut, String moisFin);

    // Tu peux ajouter d'autres méthodes si besoin :
    List<Mensualite> findByContratEmployeId(Long contratEmployeId);

    List<Mensualite> findByMensualiteSolde(boolean mensualiteSolde);

    // Optionnel : filtrer par entreprise
    List<Mensualite> findByEmployeIdAndCompanyId(Long employeId, Long companyId);

}