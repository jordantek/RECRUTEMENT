package com.tpc.tpcgestpaie.localapp.repository.paie;

import com.tpc.tpcgestpaie.localapp.model.PrelevementMensualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrelevementMensualiteRepository extends JpaRepository<PrelevementMensualite, Long> {

    // Tu peux ajouter des méthodes spécifiques si besoin, par exemple:

      @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.company.id = :companyId")
    List<PrelevementMensualite> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.employe.id = :employeId")
    List<PrelevementMensualite> findByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.mensualite.id = :mensualiteId")
    List<PrelevementMensualite> findByMensualiteId(@Param("mensualiteId") Long mensualiteId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.moisPrelevement = :mois " +
            "AND pm.institution.id = :institutionId")
    List<PrelevementMensualite> findByMoisPrelevementAndInstitutionId(@Param("mois") String mois, @Param("institutionId") Long institutionId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.moisPrelevement = :mois")
    List<PrelevementMensualite> findByMoisPrelevement(@Param("mois") String mois);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.moisPrelevement = :mois " +
            "AND pm.company.id = :companyId")
    List<PrelevementMensualite> findByMoisPrelevementAndCompanyId(@Param("mois") String mois, @Param("companyId") Long companyId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.moisPrelevement = :moisPrelevement " +
            "AND pm.company.id = :companyId " +
            "AND pm.institution.id = :institutionId")
    List<PrelevementMensualite> findByMoisPrelevementAndCompanyIdAndInstitutionId(@Param("moisPrelevement") String moisPrelevement,@Param("companyId") Long companyId,
            @Param("institutionId") Long institutionId);

    @Query("SELECT pm FROM PrelevementMensualite pm " +
            "LEFT JOIN FETCH pm.employe " +
            "LEFT JOIN FETCH pm.contratEmploye " +
            "LEFT JOIN FETCH pm.company " +
            "LEFT JOIN FETCH pm.institution " +
            "LEFT JOIN FETCH pm.mensualite " +
            "LEFT JOIN FETCH pm.lastUpdateUser " +
            "WHERE pm.moisPrelevement = :moisPrelevement " +
            "AND pm.company.id = :companyId " +
            "AND pm.contratEmploye.id = :contratEmployeId " +
            "AND pm.institution.id = :institutionId")
    PrelevementMensualite findOneByMoisPrelevementAndContratEmployeIdAndCompanyIdAndInstitutionId(
            @Param("moisPrelevement") String moisPrelevement,
            @Param("companyId") Long companyId,
            @Param("institutionId") Long institutionId,
            @Param("contratEmployeId") Long contratEmployeId
    );


}
