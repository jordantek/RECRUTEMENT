package com.tpc.tpcgestpaie.localapp.repository.paie.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.etat.NetParEntrepriseBanqueDTO;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BulletinPaieRepository extends JpaRepository<BulletinPaie, Long> {


    // Ajoutez cette méthode si elle n'existe pas
    @Query("SELECT DISTINCT b.company FROM BulletinPaie b WHERE b.company.id = :companyId")
    Optional<Company> findCompanyById(@Param("companyId") Long companyId);

    // Ou cette méthode
    List<BulletinPaie> findByCompanyId(Long companyId);

    Optional<BulletinPaie> findByEmployeIdAndMoisAndCompanyId(Long employeId, String mois, Long companyId);


    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE ce.natureContrat.id = :natureContratId
            """)
    List<BulletinPaie> findByContratEmployeNatureContratId(@Param("natureContratId") Long natureContratId);

    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND db.id = :banqueId
            """)
    List<BulletinPaie> findByMoisAndDomiciliationBancaireEmployeBanqueId(@Param("mois") String mois,
                                                                         @Param("banqueId") Long banqueId);

    @Query("""
    SELECT b FROM BulletinPaie b
    JOIN FETCH b.contratEmploye ce
    JOIN FETCH ce.natureContrat nc
    JOIN FETCH b.company c
    JOIN FETCH ce.departement d
    JOIN FETCH b.domiciliationBancaireEmploye db
    WHERE b.mois = :mois
      AND c.id = :companyId
""")
    List<BulletinPaie> findByMoisAndCompanyId(@Param("mois") String mois,
                                              @Param("companyId") Long companyId);
    @Query("""
       SELECT b FROM BulletinPaie b
       JOIN FETCH b.contratEmploye ce
       JOIN FETCH ce.natureContrat nc
       JOIN FETCH b.company c
       JOIN FETCH ce.departement d
       JOIN FETCH b.domiciliationBancaireEmploye db
       WHERE b.mois = :mois
         AND c.id = :companyId
         AND b.statut = :statut
       """)
    List<BulletinPaie> findByMoisAndCompanyIdStatus(@Param("mois") String mois,
                                                    @Param("companyId") Long companyId,
                                                    @Param("statut") String statut);

    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND c.id = :companyId
                  AND d.id = :departementId
            """)
    List<BulletinPaie> findByMoisAndCompanyIdAndDepartementId(
            @Param("mois") String mois,
            @Param("companyId") Long companyId,
            @Param("departementId") Long departementId
    );


    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND c.id = :companyId
                  AND d.id = :departementId
                  AND db.id = :banqueId
                  AND nc.id = :natureContratId
            """)
    List<BulletinPaie> findByMoisAndCompanyIdAndDepartementIdAndDomiciliationBancaireEmployeBanqueIdAndContratEmployeNatureContratId(
            @Param("mois") String mois,
            @Param("companyId") Long companyId,
            @Param("departementId") Long departementId,
            @Param("banqueId") Long banqueId,
            @Param("natureContratId") Long natureContratId
    );


    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND c.id = :companyId
                  AND nc.id = :natureContratId
            """)
    List<BulletinPaie> findByMoisAndCompanyIdAndContratEmployeNatureContratId(@Param("mois") String mois,
                                                                              @Param("companyId") Long companyId,
                                                                              @Param("natureContratId") Long natureContratId);

    // Autres requêtes similaires avec JOIN FETCH suivant ta logique...

    @Query("""
                SELECT COUNT(b) FROM BulletinPaie b
                WHERE b.mois = :mois
                  AND b.company.id = :companyId
                  AND b.contratEmploye.natureContrat.id = :natureContratId
            """)
    Long countByMoisAndEntrepriseAndNatureContrat(@Param("mois") String mois,
                                                  @Param("companyId") Long companyId,
                                                  @Param("natureContratId") Long natureContratId);

    @Query("""
                SELECT DISTINCT b.company FROM BulletinPaie b
                WHERE b.mois = :mois
                  AND b.contratEmploye.natureContrat.id <> 3
            """)
    List<Company> findDistinctCompanyByMois(@Param("mois") String mois);


    @Query("""
                SELECT DISTINCT b.company FROM BulletinPaie b
                WHERE b.mois = :mois
                  AND b.company.tvaVal = :tvaVal
                  AND b.contratEmploye.natureContrat.id <> 3
            """)
    List<Company> findDistinctCompanyByMoisAndTvaVal(@Param("mois") String mois,
                                                     @Param("tvaVal") java.math.BigDecimal tvaVal);

    @Query("""
                SELECT DISTINCT b.company FROM BulletinPaie b
                WHERE b.mois = :mois
                  AND b.contratEmploye.natureContrat.id = 3
            """)
    List<Company> findDistinctCompanyByMoisHonoraire(@Param("mois") String mois);

    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND b.employe.id = :employeId
            """)
    BulletinPaie findByMoisAndEmployeId(@Param("mois") String mois,
                                        @Param("employeId") Long employeId);

    @Query("""
                SELECT b FROM BulletinPaie b
                JOIN FETCH b.contratEmploye ce
                JOIN FETCH ce.natureContrat nc
                JOIN FETCH b.company c
                JOIN FETCH ce.departement d
                JOIN FETCH b.domiciliationBancaireEmploye db
                WHERE b.mois = :mois
                  AND b.employe.id = :employeId
                  AND nc.id = :natureContratId
            """)
    BulletinPaie findByMoisAndEmployeIdAndContratEmployeNatureContratId(@Param("mois") String mois,
                                                                        @Param("employeId") Long employeId,
                                                                        @Param("natureContratId") Long natureContratId);



    @Transactional
    @Modifying
    @Query("""
    DELETE FROM BulletinPaie b
    WHERE b.company.id = :companyId
      AND b.mois = :mois
      AND b.statut = :statut
     """)
    int deleteByCompanyIdAndMoisAndStatut(@Param("companyId") Long companyId,
                                          @Param("mois") String mois,
                                          @Param("statut") String statut);
    // BulletinPaieRepository.java
    List<BulletinPaie> findByEmployeIdAndDateCalculSalaireBetweenOrderByDateCalculSalaireAsc(Long employeId, LocalDate start, LocalDate end);

    List<BulletinPaie> findByEmployeIdAndMoisInOrderByMoisAsc(Long employeId, List<String> mois);

    // Salaire brut du bulletin
    @Query("SELECT b.salaireBrut FROM BulletinPaie b WHERE b.contratEmploye.id = :contratEmployeId AND b.mois = :mois")
    Optional<BigDecimal> findSalaireBrutByContratEmployeIdAndMois(@Param("contratEmployeId") Long contratEmployeId,
                                                                  @Param("mois") String mois);

    @Query("SELECT b FROM BulletinPaie b WHERE b.company.id = :companyId AND YEAR(b.dateCalculSalaire) = :annee AND b.statut = :statut")
    List<BulletinPaie> findByCompanyIdAndYearAndStatut(@Param("companyId") Long companyId,
                                                       @Param("annee") int annee,
                                                       @Param("statut") String statut);


    // Montant total net payé (tous bulletins validés)
    @Query("SELECT COALESCE(SUM(b.netAPayer), 0) FROM BulletinPaie b WHERE b.company.id = :companyId AND b.statut ='Validé' ")
    BigDecimal sumNetPayeByCompany(@Param("companyId") Long companyId);



    // Montant total net restant à payer (statut PENDING)
    @Query("SELECT COALESCE(SUM(b.netAPayer), 0) FROM BulletinPaie b WHERE b.company.id = :companyId AND b.statut = 'En attente'")
    BigDecimal sumNetPendingByCompany(@Param("companyId") Long companyId);

    // Net par statut
    @Query("""
    SELECT 
        COALESCE(SUM(b.netAPayer),0),
        COALESCE(SUM(b.autreAvantage),0),
        COALESCE(SUM(b.autreRetenue),0),
        COALESCE(SUM(b.montantCnss),0),
        COALESCE(SUM(b.montantIpts),0),
        COALESCE(SUM(b.montantAib),0)
    FROM BulletinPaie b
    WHERE b.company.id = :companyId AND b.statut = :statut
""")
    List<Object[]> sumNetEtPrimesRetenuesByCompanyAndStatut(@Param("companyId") Long companyId,
                                                            @Param("statut") String statut);

    List<BulletinPaie> findByCompanyIdAndMois(Long companyId, String mois);


    @Query("SELECT b FROM BulletinPaie b " +
            "WHERE b.company.id = :companyId " +
            "AND b.employe.id = :employeId " +
            "AND b.mois BETWEEN :debut AND :fin")
    List<BulletinPaie> findByCompanyAndEmployeAndMoisBetween(@Param("companyId") Long companyId,
                                                             @Param("employeId") Long employeId,
                                                             @Param("debut") String debut,
                                                             @Param("fin") String fin);


    List<BulletinPaie> findByCompanyIdAndMoisBetween(Long companyId, String debut, String fin);

    @Query("""
        SELECT new com.tpc.tpcgestpaie.localapp.dto.etat.NetParEntrepriseBanqueDTO(
                   bu.mois,
                   b.id,
                   b.name,
                   c.name,
                   SUM(bu.netAPayer)
               )
        FROM BulletinPaie bu
        JOIN bu.company c
        JOIN bu.domiciliationBancaireEmploye b
        WHERE b.id = :banqueId AND bu.mois = :mois
        GROUP BY bu.mois, b.id, b.name, c.name
        """)
    List<NetParEntrepriseBanqueDTO> getTotauxParEntrepriseEtBanque(
            @Param("banqueId") Long banqueId,
            @Param("mois") String mois
    );

    @Query("""
        SELECT COALESCE(SUM(bu.netAPayer),0)
        FROM BulletinPaie bu
        JOIN bu.domiciliationBancaireEmploye b
        WHERE b.id = :banqueId AND bu.mois = :mois
        """)
    BigDecimal getTotalGlobalParBanqueEtMois(
            @Param("banqueId") Long banqueId,
            @Param("mois") String mois
    );

//    List<BulletinPaie> findByEmployeIdAndMoisBetween(Long employeId, String moisDebut, String moisFin);

    @Query("SELECT b FROM BulletinPaie b " +
            "LEFT JOIN FETCH b.employe " +
            "LEFT JOIN FETCH b.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH b.company " +
            "LEFT JOIN FETCH b.domiciliationBancaireEmploye " +
            "WHERE b.employe.id = :employeId AND b.mois BETWEEN :moisDebut AND :moisFin")

    List<BulletinPaie> findByEmployeAndMoisBetween(
            @Param("employeId") Long employeId,
            @Param("moisDebut") String moisDebut,
            @Param("moisFin") String moisFin
    );


    @Query("SELECT b FROM BulletinPaie b " +
            "LEFT JOIN FETCH b.employe " +
            "LEFT JOIN FETCH b.contratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH b.company " +
            "LEFT JOIN FETCH b.domiciliationBancaireEmploye " +
            "WHERE b.company.id = :companyId AND b.mois = :mois")
    List<BulletinPaie> findByCompanyIdAndMoisWithRelations(@Param("companyId") Long companyId, @Param("mois") String mois);


    @Query("SELECT b FROM BulletinPaie b " +
            "WHERE b.company.id = :companyId " +
            "AND b.mois >= :moisDebut " +
            "AND b.mois <= :moisFin " +
            "ORDER BY b.mois, b.employe.nom")
    List<BulletinPaie> findByCompanyIdAndMoisBetweenPourBulletin(@Param("companyId") Long companyId,
                                                     @Param("moisDebut") String moisDebut,
                                                     @Param("moisFin") String moisFin);



    @Query("SELECT b FROM BulletinPaie b " +
            "WHERE b.employe.id = :employeId " +
            "AND b.company.id = :companyId " +
            "AND b.mois >= :moisDebut AND b.mois <= :moisFin " +
            "ORDER BY b.mois ASC")
    List<BulletinPaie> findByEmployeIdAndCompanyIdAndMoisBetween(
            @Param("employeId") Long employeId,
            @Param("companyId") Long companyId,
            @Param("moisDebut") String moisDebut,
            @Param("moisFin") String moisFin);

    @Query("SELECT b FROM BulletinPaie b " +
            "JOIN b.contratEmploye c " +
            "WHERE c.departement.id = :departementId " +
            "AND b.company.id = :companyId " +
            "AND b.mois >= :moisDebut " +
            "AND b.mois <= :moisFin " +
            "ORDER BY b.mois ASC")
    List<BulletinPaie> findByDepartementAndPeriode(
            @Param("departementId") Long departementId,
            @Param("companyId") Long companyId,
            @Param("moisDebut") String moisDebut,
            @Param("moisFin") String moisFin);


    Optional<BulletinPaie> findByContratEmploye_IdAndMoisAndStatut(
            Long contratEmployeId,
            String mois,
            String statut
    );

    List<BulletinPaie> findByEmployeIdOrderByMoisDesc(Long employeId);

}
