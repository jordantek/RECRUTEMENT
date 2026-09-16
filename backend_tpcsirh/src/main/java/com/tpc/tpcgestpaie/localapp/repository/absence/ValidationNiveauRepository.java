package com.tpc.tpcgestpaie.localapp.repository.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.ValidationNiveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValidationNiveauRepository extends JpaRepository<ValidationNiveau, Long> {

    List<ValidationNiveau> findByDemandeAbsenceIdOrderByOrdreNiveauAsc(Long demandeAbsenceId);

    Optional<ValidationNiveau> findByDemandeAbsenceIdAndOrdreNiveau(Long demandeAbsenceId, Integer ordreNiveau);

    // Trouver toutes les validations d'un employé (validateur)
    // Ou si tu veux trier par la date de création de la validation elle-même
    List<ValidationNiveau> findByValidateurIdOrderByCreatedAtDesc(Long validateurId);
    // Alternative : trouver par ordre et demande


    // Alternative avec tri par date de la demande (si préféré)
    @Query("SELECT v FROM ValidationNiveau v " +
            "JOIN FETCH v.demandeAbsence d " +
            "JOIN FETCH d.employeDemandeur emp " +
            "LEFT JOIN FETCH d.typeAbsence ta " +
            "WHERE v.validateur.id = :validateurId " +
            "ORDER BY d.createdAt DESC, v.createdAt DESC")
    List<ValidationNiveau> findHistoriqueByValidateurOrderByDemandeDate(@Param("validateurId") Long validateurId);
    // Trouver les validations en attente pour un validateur spécifique
    @Query("SELECT v FROM ValidationNiveau v " +
            "JOIN v.demandeAbsence d " +
            "WHERE v.validateur.id = :validateurId " +
            "AND d.niveauValidationEnCours = v.ordreNiveau " +
            "AND v.statut = 'EN_ATTENTE'")
    List<ValidationNiveau> findEnAttentePourValidateur(@Param("validateurId") Long validateurId);

    @Query("SELECT v FROM ValidationNiveau v " +
            "JOIN FETCH v.demandeAbsence d " +
            "JOIN FETCH d.employeDemandeur emp " +
            "LEFT JOIN FETCH d.typeAbsence ta " +
            "WHERE v.validateur.id = :validateurId " +
            "ORDER BY v.createdAt DESC")
    List<ValidationNiveau> findHistoriqueByValidateur(@Param("validateurId") Long validateurId);


    List<ValidationNiveau> findByDemandeAbsenceId(Long demandeAbsenceId);


    Optional<ValidationNiveau> findTopByDemandeAbsenceIdOrderByOrdreNiveauDesc(Long demandeAbsenceId);


    /**
     * Récupère les validations approuvées d'une demande, ordonnées du niveau le plus élevé au plus bas
     */
    List<ValidationNiveau> findByDemandeAbsenceIdAndStatutOrderByOrdreNiveauDesc(
            Long demandeAbsenceId, String statut);

}