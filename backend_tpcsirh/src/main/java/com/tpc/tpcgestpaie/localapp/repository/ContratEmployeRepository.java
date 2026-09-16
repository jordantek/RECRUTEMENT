package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeContratActifDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Departement;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ContratEmployeRepository extends JpaRepository<ContratEmploye, Long> {


    // ⭐ NOUVELLE méthode paginée
    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        LEFT JOIN FETCH c.added_by u
    """)
    Page<ContratEmploye> findByCompanyId(Long companyId, Pageable pageable);
    // AJOUT: Méthode de pagination générique
    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        LEFT JOIN FETCH c.added_by u
    """)
    Page<ContratEmploye> findAllWithJoins(Pageable pageable);

    // AJOUT: Pagination pour les contrats par entreprise
    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE comp.id = :companyId
    """)
    Page<ContratEmploye> findByCompanyIdPaginated(@Param("companyId") Long companyId, Pageable pageable);

    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE comp.id = :companyId
       AND (
        LOWER(e.nom) LIKE %:search%
        OR LOWER(e.prenom) LIKE %:search%
        OR LOWER(e.matricule) LIKE %:search%
        OR LOWER(c.poste.libelle) LIKE %:search%
    )
    """)
    Page<ContratEmploye> searchByCompanyId(@Param("companyId") Long companyId,  @Param("search") String search, Pageable pageable);


    // AJOUT: Pagination pour les contrats en cours de validité
    @Query("""
        SELECT c FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE c.date_debut <= CURRENT_DATE
        AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE)
        AND c.arretContrat = false
    """)
    Page<ContratEmploye> findAllContratEmployeEnCoursDeValiditePaginated(Pageable pageable);

    // AJOUT: Pagination pour les contrats en cours de validité par entreprise
    @Query("""
        SELECT c FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH e.formations f
        LEFT JOIN FETCH comp.managedCompanies mc
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE comp.id = :companyId
        AND c.date_debut <= CURRENT_DATE
        AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE)
        AND c.arretContrat = false
    """)
    Page<ContratEmploye> findAllContratEmployeEnCoursDeValiditeParEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);



    // AJOUT: Pagination pour les contrats non arrêtés
    @Query("""
        SELECT c FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE c.arretContrat = false
    """)
    Page<ContratEmploye> findAllContratEmployeNonArretePaginated(Pageable pageable);

    // AJOUT: Pagination pour les contrats non arrêtés par entreprise
    @Query("""
        SELECT c FROM ContratEmploye c
        LEFT JOIN FETCH c.employe e
        LEFT JOIN FETCH c.company comp
        LEFT JOIN FETCH c.departement d
        LEFT JOIN FETCH c.poste p
        LEFT JOIN FETCH c.categorieEmploye ce
        LEFT JOIN FETCH c.natureContrat nc
        LEFT JOIN FETCH c.modeDePaiement mdp
        LEFT JOIN FETCH c.banque b
        WHERE c.company.id = :companyId 
        AND c.arretContrat = false
    """)
    Page<ContratEmploye> findAllContratEmployeNonArreteByEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);

    // AJOUT: Pagination pour les contrats par employé
    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.departement
        LEFT JOIN FETCH c.company
        LEFT JOIN FETCH c.poste
        LEFT JOIN FETCH c.categorieEmploye
        LEFT JOIN FETCH c.natureContrat
        LEFT JOIN FETCH c.modeDePaiement
        LEFT JOIN FETCH c.banque
        LEFT JOIN FETCH c.added_by
        LEFT JOIN FETCH c.employe
        WHERE c.employe.id = :employeId
        ORDER BY c.createdAt DESC
    """)
    Page<ContratEmploye> findAllByEmployeWithAllJoinsOrderedPaginated(@Param("employeId") Long employeId, Pageable pageable);

    // AJOUT: Pagination pour les employés avec contrat actif
    @Query("""
        SELECT new com.tpc.tpcgestpaie.localapp.dto.employe.EmployeContratActifDTO(
            e.nom,
            e.prenom,
            e.id,
            c.id,
            e.matricule,
            c.company.id
        )
        FROM ContratEmploye c
        JOIN c.employe e
        WHERE c.company.id = :companyId
          AND c.date_debut <= CURRENT_DATE
          AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE)
          AND c.status_contrat = 'CONTRAT EN COURS'
    """)
    Page<EmployeContratActifDTO> findEmployesAvecContratActifParEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);


    // AJOUT: Pagination pour les contrats non arrêtés par entreprise et département
    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.company.id = :companyId
    AND c.departement.id = :departementId
    AND c.arretContrat = false
""")
    Page<ContratEmploye> findAllContratEmployeNonArreteByEntrepriseAndDepartementPaginated(
            @Param("companyId") Long companyId,
            @Param("departementId") Long departementId,
            Pageable pageable
    );
    //FIN PAGINATION
    // [Toutes tes méthodes existantes restent ici]

    // Ajoutez cette méthode pour charger avec les relations
    @Query("SELECT ce FROM ContratEmploye ce " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.employe " +
            "WHERE ce.id = :id")
    Optional<ContratEmploye> findByIdWithRelations(@Param("id") Long id);

    @Query("""
       SELECT DISTINCT c FROM ContratEmploye c
            LEFT JOIN FETCH c.employe
            LEFT JOIN FETCH c.company
            LEFT JOIN FETCH c.departement
            LEFT JOIN FETCH c.poste
            LEFT JOIN FETCH c.categorieEmploye
            LEFT JOIN FETCH c.natureContrat
            LEFT JOIN FETCH c.modeDePaiement
            LEFT JOIN FETCH c.banque
            LEFT JOIN FETCH c.added_by
            LEFT JOIN FETCH c.rubriques r
            LEFT JOIN FETCH r.rubrique
            WHERE c.id = :id
    """)
    Optional<ContratEmploye> findByIdWithFullRelations(Long id);

    @Query("SELECT c FROM ContratEmploye c " +
            "WHERE c.type_contrat = :typeContrat " +
            "AND c.date_fin IS NOT NULL " +
            "AND c.arretContrat = false")
    List<ContratEmploye> findAllActiveCddWithDateFin(@Param("typeContrat") String typeContrat);


    @Query("SELECT c FROM ContratEmploye c " +
            "WHERE c.employe.id = :employeId " +
            "ORDER BY c.date_fin DESC")
    ContratEmploye findLastByEmployeId(@Param("employeId") Long employeId);


    boolean existsByEmployeIdAndCompanyId(Long employeId, Long companyId);

    // Tu peux ajouter des méthodes personnalisées ici si besoin
    @Query("""
        SELECT c
        FROM ContratEmploye c
        LEFT JOIN FETCH c.departement
        LEFT JOIN FETCH c.company
        LEFT JOIN FETCH c.poste
        LEFT JOIN FETCH c.categorieEmploye
        LEFT JOIN FETCH c.natureContrat
        LEFT JOIN FETCH c.modeDePaiement
        LEFT JOIN FETCH c.banque
        LEFT JOIN FETCH c.added_by
        LEFT JOIN FETCH c.employe
        WHERE c.id = :id
        """)
    Optional<ContratEmploye> findById(Long id);

    @Query("""
               SELECT DISTINCT c 
               FROM ContratEmploye c
               LEFT JOIN FETCH c.employe e
               LEFT JOIN FETCH c.banque b
               LEFT JOIN FETCH c.modeDePaiement mdp
               LEFT JOIN FETCH c.natureContrat n
              LEFT JOIN FETCH c.categorieEmploye ce
              LEFT JOIN FETCH   c.company comp
              LEFT JOIN FETCH c.departement d
              LEFT JOIN FETCH c.poste p
              LEFT JOIN FETCH c.added_by u
              WHERE c.employe.id = :employeId 
             AND c.arretContrat = false
            """)
    Optional<ContratEmploye> findActifByEmployeId(@Param("employeId") Long employeId);

    @Query("""
               SELECT DISTINCT c 
               FROM ContratEmploye c
               LEFT JOIN FETCH c.employe e
               LEFT JOIN FETCH c.banque b
               LEFT JOIN FETCH c.modeDePaiement mdp
               LEFT JOIN FETCH c.natureContrat n
              LEFT JOIN FETCH c.categorieEmploye ce
              LEFT JOIN FETCH   c.company comp
              LEFT JOIN FETCH c.departement d
              LEFT JOIN FETCH c.poste p
              LEFT JOIN FETCH c.added_by u
              WHERE c.employe.id = :employeId 
            AND c.date_debut <= :mois
            AND (c.date_arret_contrat IS NULL OR c.date_arret_contrat >= :mois)
            """)
    Optional<ContratEmploye> findActifByEmployeIdMois(@Param("employeId") Long employeId, @Param("mois") LocalDate mois);
    // Récupérer tous les contrats par entreprise (companyId)
    @Query("""
               SELECT DISTINCT c 
               FROM ContratEmploye c
               LEFT JOIN FETCH c.employe e
               LEFT JOIN FETCH c.banque b
               LEFT JOIN FETCH c.modeDePaiement mdp
               LEFT JOIN FETCH c.natureContrat n
              LEFT JOIN FETCH c.categorieEmploye ce
              LEFT JOIN FETCH   c.company comp
              LEFT JOIN FETCH c.departement d
              LEFT JOIN FETCH c.poste p
              LEFT JOIN FETCH c.added_by u
               WHERE c.company.id = :companyId
            """)
    List<ContratEmploye> findByCompanyId(@Param("companyId") Long companyId);


    @Query("""
       SELECT DISTINCT c 
       FROM ContratEmploye c
       LEFT JOIN FETCH c.employe e
       LEFT JOIN FETCH c.banque b
       LEFT JOIN FETCH c.modeDePaiement mdp
       LEFT JOIN FETCH c.natureContrat n
       LEFT JOIN FETCH c.categorieEmploye ce
       LEFT JOIN FETCH c.company comp
       LEFT JOIN FETCH c.departement d
       LEFT JOIN FETCH c.poste p
       LEFT JOIN FETCH c.added_by u
       WHERE c.employe.id = :employeId
         AND c.company.id = :companyId
         AND c.arretContrat = false
      """)
    Optional<ContratEmploye> findActifByEmployeIdAndCompanyId(@Param("employeId") Long employeId,
                                                              @Param("companyId") Long companyId);

    List<ContratEmploye> findByEmploye(Employe employe);
    @Query("""
             SELECT c
             FROM ContratEmploye c
             LEFT JOIN FETCH c.departement
             LEFT JOIN FETCH c.company
             LEFT JOIN FETCH c.poste
             LEFT JOIN FETCH c.categorieEmploye
             LEFT JOIN FETCH c.natureContrat
             LEFT JOIN FETCH c.modeDePaiement
             LEFT JOIN FETCH c.banque
             LEFT JOIN FETCH c.added_by
             LEFT JOIN FETCH c.employe
             WHERE c.employe = :employe
             AND c.status_contrat = :statut \s
             ORDER BY c.createdAt DESC
            \s""")

    List<ContratEmploye> findByEmployeWithAll(@Param("employe") Employe employe, @Param("statut") String statut);
    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.company
                LEFT JOIN FETCH c.poste
                LEFT JOIN FETCH c.categorieEmploye
                LEFT JOIN FETCH c.natureContrat
                LEFT JOIN FETCH c.modeDePaiement
                LEFT JOIN FETCH c.banque
                LEFT JOIN FETCH c.added_by
                LEFT JOIN FETCH c.employe e
                WHERE c.employe = :employe
                AND c.arretContrat = false
                ORDER BY c.createdAt DESC
            """)
    List<ContratEmploye> findActiveByEmployeWithAllJoinsOrdered(@Param("employe") Employe employe);

    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.departement
                LEFT JOIN FETCH c.company
                LEFT JOIN FETCH c.poste
                LEFT JOIN FETCH c.categorieEmploye
                LEFT JOIN FETCH c.natureContrat
                LEFT JOIN FETCH c.modeDePaiement
                LEFT JOIN FETCH c.banque
                LEFT JOIN FETCH c.added_by
                LEFT JOIN FETCH c.employe
                WHERE c.employe = :employe
                  AND c.arretContrat = false
                ORDER BY c.createdAt DESC
            """)
    Optional<ContratEmploye> findTopByEmployeAndArretContratFalseOrderByCreatedAtDesc(@Param("employe") Employe employe);

    // Liste des contrats en cours de validite ou actif
    @Query("SELECT c FROM ContratEmploye c WHERE c.date_debut <= CURRENT_DATE AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE) AND c.status_contrat = 'ACTIF'")
    List<ContratEmploye> findAllContratEmployeEnCoursDeValidite();

    @Query("""
             SELECT c FROM ContratEmploye c
             JOIN FETCH c.employe e
            JOIN FETCH c.company comp
            LEFT JOIN FETCH c.departement d
             LEFT JOIN FETCH c.poste p
               LEFT JOIN FETCH c.banque b
               LEFT JOIN FETCH c.modeDePaiement mdp
               LEFT JOIN FETCH c.natureContrat n
              LEFT JOIN FETCH c.categorieEmploye ce
             WHERE comp.id = :companyId
             AND c.date_debut <= CURRENT_DATE
             AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE)
             AND c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeEnCoursDeValiditeParEntreprise(Long companyId);

    @Query("""
    SELECT c FROM ContratEmploye c
    JOIN FETCH c.employe e
    JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.banque b
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.natureContrat n
    LEFT JOIN FETCH c.categorieEmploye ce
    WHERE comp.id = :companyId
      AND c.date_debut <= :mois
      AND (c.date_arret_contrat IS NULL OR c.date_arret_contrat >= :mois)
""")
    List<ContratEmploye> findContratsPourCalculSalaireParMois(
            @Param("companyId") Long companyId,
            @Param("mois") LocalDate mois
    );

    //Liste des contrats non arrêté
    @Query("""
             SELECT c FROM ContratEmploye c
             JOIN FETCH c.employe e
            JOIN FETCH c.company comp
            LEFT JOIN FETCH c.departement d
             LEFT JOIN FETCH c.poste p
               LEFT JOIN FETCH c.banque b
               LEFT JOIN FETCH c.modeDePaiement mdp
               LEFT JOIN FETCH c.natureContrat n
              LEFT JOIN FETCH c.categorieEmploye ce
            WHERE c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeNonArrete();

    @Query("""
            SELECT c FROM ContratEmploye c
            LEFT JOIN FETCH c.employe
            LEFT JOIN FETCH c.company
            LEFT JOIN FETCH c.departement
            LEFT JOIN FETCH c.poste
            LEFT JOIN FETCH c.categorieEmploye
            LEFT JOIN FETCH c.natureContrat
            LEFT JOIN FETCH c.modeDePaiement
            LEFT JOIN FETCH c.banque
          WHERE c.company.id = :companyId AND c.arretContrat = false
            """)
     List<ContratEmploye> findAllContratEmployeNonArreteByEntreprise(@Param("companyId") Long companyId);

    @Query("""
    SELECT c FROM ContratEmploye c
    JOIN FETCH c.employe e
    JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.banque b
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.natureContrat n
    LEFT JOIN FETCH c.categorieEmploye ce
    WHERE comp.id = :companyId
      AND c.date_debut <= :mois
      AND (c.date_arret_contrat IS NULL OR c.date_arret_contrat >= :mois)
""")
    List<ContratEmploye> findAllContratEmployeNonArreteByEntrepriseMois(@Param("companyId") Long companyId, @Param("mois") LocalDate mois);
    @Query("""
    SELECT c FROM ContratEmploye c
    JOIN FETCH c.employe e
    JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.banque b
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.natureContrat n
    LEFT JOIN FETCH c.categorieEmploye ce
    WHERE comp.id = :companyId
      AND c.date_debut <= :mois
      AND (c.date_arret_contrat IS NULL OR c.date_arret_contrat >= :mois)
""")
    List<ContratEmploye> findAllContratEmployeNonArreteByEntrepriseAndDepartementMois(@Param("companyId") Long companyId, @Param("departementId") Long departementId, @Param("mois") LocalDate mois);

    @Query("""
            SELECT c FROM ContratEmploye c
            LEFT JOIN FETCH c.employe
            LEFT JOIN FETCH c.company
            LEFT JOIN FETCH c.departement
            LEFT JOIN FETCH c.poste
            LEFT JOIN FETCH c.categorieEmploye
            LEFT JOIN FETCH c.natureContrat
            LEFT JOIN FETCH c.modeDePaiement
            LEFT JOIN FETCH c.banque
            WHERE c.company.id = :companyId
            AND c.departement.id = :departementId
            AND c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteByEntrepriseAndDepartement(@Param("companyId") Long companyId, @Param("departementId") Long departementId);


    @Query("""
            SELECT c FROM ContratEmploye c
            LEFT JOIN FETCH c.employe
            LEFT JOIN FETCH c.company
            LEFT JOIN FETCH c.departement
            LEFT JOIN FETCH c.poste
            LEFT JOIN FETCH c.categorieEmploye
            LEFT JOIN FETCH c.natureContrat
            LEFT JOIN FETCH c.modeDePaiement
            LEFT JOIN FETCH c.banque
            WHERE c.arretContrat = false
            AND c.date_debut >= :dateDebut
            AND c.date_fin <= :dateFin
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteByPeriode(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    @Query("""
            SELECT c FROM ContratEmploye c
            LEFT JOIN FETCH c.employe
            LEFT JOIN FETCH c.company
            LEFT JOIN FETCH c.departement
            LEFT JOIN FETCH c.poste
            LEFT JOIN FETCH c.categorieEmploye
            LEFT JOIN FETCH c.natureContrat
            LEFT JOIN FETCH c.modeDePaiement
            LEFT JOIN FETCH c.banque
            WHERE c.arretContrat = false
            AND c.fin_essai BETWEEN :dateDebut AND :dateFin
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteFinPeriodeEssai(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT COUNT(c) FROM ContratEmploye c WHERE c.company = :company AND c.status_contrat = 'ARRETE' AND c.date_debut >= :dateDebut AND c.date_fin <= :dateFin")
    Long countContratEmployeArreteParPeriodeEtParEntreprise(@Param("company") Company company, @Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);


    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.employe e
                LEFT JOIN FETCH c.company comp
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.poste p
                LEFT JOIN FETCH c.categorieEmploye cat
                LEFT JOIN FETCH c.natureContrat nc
                LEFT JOIN FETCH c.modeDePaiement mp
                LEFT JOIN FETCH c.banque b
                LEFT JOIN FETCH c.added_by u
                WHERE c.company = :company 
                AND c.departement = :departement 
                AND c.employe = :employe
                AND c.date_arret_contrat IS NULL
                AND c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteByEntrepriseDepartementEtEmploye(@Param("company") Company company, @Param("departement") Departement departement, @Param("employe") Employe employe);

    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.employe e
                LEFT JOIN FETCH c.company comp
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.poste p
                LEFT JOIN FETCH c.categorieEmploye cat
                LEFT JOIN FETCH c.natureContrat nc
                LEFT JOIN FETCH c.modeDePaiement mp
                LEFT JOIN FETCH c.banque b
                LEFT JOIN FETCH c.added_by u
                WHERE c.employe = :employe
                  AND c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteByEmploye(@Param("employe") Employe employe);

    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.employe e
                LEFT JOIN FETCH c.company comp
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.poste p
                LEFT JOIN FETCH c.categorieEmploye cat
                LEFT JOIN FETCH c.natureContrat nc
                LEFT JOIN FETCH c.modeDePaiement mp
                LEFT JOIN FETCH c.banque b
                LEFT JOIN FETCH c.added_by u
                WHERE e.id = :employeId
                  AND c.arretContrat = false
            """)
    List<ContratEmploye> findAllContratEmployeNonArreteByEmployeId(@Param("employeId") Long employeId);

    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.employe e
                LEFT JOIN FETCH c.company comp
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.poste p
                LEFT JOIN FETCH c.categorieEmploye cat
                LEFT JOIN FETCH c.natureContrat nc
                LEFT JOIN FETCH c.modeDePaiement mp
                LEFT JOIN FETCH c.banque b
                LEFT JOIN FETCH c.added_by u
                WHERE c.employe = :employe
            """)
    List<ContratEmploye> findAllContratEmployeByEmploye(@Param("employe") Employe employe);

    @Query("SELECT COUNT(c) FROM ContratEmploye c " + "WHERE c.company = :company " + "AND c.date_debut <= CURRENT_DATE " + "AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE) " + "AND c.status_contrat = 'ACTIF'")
    Long countContratEmployeEnCoursDeValiditeParEntreprise(@Param("company") Company company);

    @Query("""
                SELECT new com.tpc.tpcgestpaie.localapp.dto.employe.EmployeContratActifDTO(
                    e.nom,
                    e.prenom,
                    e.id,
                    c.id,
                    e.matricule,
                    c.company.id
                )
                FROM ContratEmploye c
                JOIN c.employe e
                WHERE c.company.id = :companyId
                  AND c.date_debut <= CURRENT_DATE
                  AND (c.date_fin IS NULL OR c.date_fin >= CURRENT_DATE)
                  AND c.status_contrat = 'CONTRAT EN COURS'
            """)
    List<EmployeContratActifDTO> findEmployesAvecContratActifParEntreprise(@Param("companyId") Long companyId);

    @Query("""
                SELECT c
                FROM ContratEmploye c
                LEFT JOIN FETCH c.employe e
                LEFT JOIN FETCH c.company comp
                LEFT JOIN FETCH c.departement d
                LEFT JOIN FETCH c.poste p
                LEFT JOIN FETCH c.categorieEmploye cat
                LEFT JOIN FETCH c.natureContrat nc
                LEFT JOIN FETCH c.modeDePaiement mp
                LEFT JOIN FETCH c.banque b
                LEFT JOIN FETCH c.added_by u             
               LEFT JOIN FETCH c.rubriques WHERE c.id = :id               
            """)
    Optional<ContratEmploye> findByIdWithRubriques(@Param("id") Long id);

    List<ContratEmploye> findByEmployeIdAndCompanyId(Long employeId, Long companyId);


    // Total des employés actifs par entreprise
    @Query("SELECT COUNT(DISTINCT c.employe.id) " +
            "FROM ContratEmploye c " +
            "WHERE c.arretContrat = false " +
            "AND c.status_contrat = 'CONTRAT EN COURS' " +
            "AND c.company.id = :entrepriseId")
    Long countEmployesActifs(@Param("entrepriseId") Long entrepriseId);

    // Répartition par genre
    @Query("SELECT e.sexe, COUNT(DISTINCT c.employe.id) " +
            "FROM ContratEmploye c " +
            "JOIN c.employe e " +
            "WHERE c.arretContrat = false " +
            "AND c.status_contrat = 'CONTRAT EN COURS' " +
            "AND c.company.id = :entrepriseId " +
            "GROUP BY e.sexe")
    List<Object[]> countByGenre(@Param("entrepriseId") Long entrepriseId);

    // Répartition par catégorie
    @Query("SELECT c.categorieEmploye.name, COUNT(DISTINCT c.employe.id) " +
            "FROM ContratEmploye c " +
            "WHERE c.arretContrat = false " +
            "AND c.status_contrat = 'CONTRAT EN COURS' " +
            "AND c.company.id = :entrepriseId " +
            "GROUP BY c.categorieEmploye.name")
    List<Object[]> countByCategorie(@Param("entrepriseId") Long entrepriseId);

    // Répartition par tranche d’âge
    @Query("""
    SELECT 
        CASE 
            WHEN (YEAR(CURRENT_DATE) - YEAR(e.date_naissance)) < 30 THEN '<30'
            WHEN (YEAR(CURRENT_DATE) - YEAR(e.date_naissance)) BETWEEN 30 AND 50 THEN '30-50'
            ELSE '>50'
        END AS trancheAge,
        COUNT(DISTINCT c.employe.id) AS total
    FROM ContratEmploye c
    JOIN c.employe e
    WHERE c.arretContrat = false
      AND c.status_contrat = 'CONTRAT EN COURS'
      AND c.company.id = :entrepriseId
    GROUP BY 
        CASE 
            WHEN (YEAR(CURRENT_DATE) - YEAR(e.date_naissance)) < 30 THEN '<30'
            WHEN (YEAR(CURRENT_DATE) - YEAR(e.date_naissance)) BETWEEN 30 AND 50 THEN '30-50'
            ELSE '>50'
        END
""")
    List<Object[]> countByTrancheAge(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT c FROM ContratEmploye c " +
            "WHERE c.company.id = :entrepriseId AND c.arretContrat = false")
    List<ContratEmploye> findByCompanyIdAndActifTrue(@Param("entrepriseId") Long entrepriseId);

    // Nombre de départs uniques
    @Query("""
    SELECT COUNT(DISTINCT c.employe.id)
    FROM ContratEmploye c
    WHERE c.company.id = :companyId
      AND c.arretContrat = true
      AND c.date_fin = (
          SELECT MAX(c2.date_fin) 
          FROM ContratEmploye c2 
          WHERE c2.employe.id = c.employe.id
      )
""")
    long countDepartUnique(@Param("companyId") Long companyId);

    // Nombre d'entrants uniques (NOUVEAU ou RENOUVELLEMENT)
    @Query("""
    SELECT COUNT(DISTINCT c.employe.id)
    FROM ContratEmploye c
    WHERE c.company.id = :companyId
      AND c.arretContrat = false
      AND c.status_contrat= "CONTRAT EN COURS"
      AND c.date_debut = (
          SELECT MAX(c2.date_debut) 
          FROM ContratEmploye c2 
          WHERE c2.employe.id = c.employe.id
      )
""")
    long countEntrantsUnique(@Param("companyId") Long companyId);


    // Employés actifs
    @Query("""
    SELECT c
    FROM ContratEmploye c
    WHERE c.company.id = :companyId
      AND c.arretContrat = false
       AND c.status_contrat= "CONTRAT EN COURS"
      AND c.date_debut = (
          SELECT MAX(c2.date_debut)
          FROM ContratEmploye c2
          WHERE c2.employe.id = c.employe.id
      )
""")
    List<ContratEmploye> findEmployesActifs(@Param("companyId") Long companyId);


    @Query("""
    SELECT c
    FROM ContratEmploye c
    LEFT JOIN FETCH c.departement
    LEFT JOIN FETCH c.company
    LEFT JOIN FETCH c.poste
    LEFT JOIN FETCH c.categorieEmploye
    LEFT JOIN FETCH c.natureContrat
    LEFT JOIN FETCH c.modeDePaiement
    LEFT JOIN FETCH c.banque
    LEFT JOIN FETCH c.added_by
    LEFT JOIN FETCH c.employe
    WHERE c.employe.id = :employeId
    ORDER BY c.createdAt DESC
""")
    List<ContratEmploye> findAllByEmployeWithAllJoinsOrdered(@Param("employeId") Long employeId);

    // Charger TOUTES les relations nécessaires pour la génération PDF
    @Query("SELECT ce FROM ContratEmploye ce " +
            "LEFT JOIN FETCH ce.employe " +
            "LEFT JOIN FETCH ce.company " +
            "LEFT JOIN FETCH ce.natureContrat " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.rubriques " +
            "WHERE ce.id = :id")
    Optional<ContratEmploye> findByIdWithAllRelations(@Param("id") Long id);

    @Query("SELECT ce FROM ContratEmploye ce " +
            "LEFT JOIN FETCH ce.employe " +
            "LEFT JOIN FETCH ce.company " +
            "LEFT JOIN FETCH ce.natureContrat " +
            "LEFT JOIN FETCH ce.poste " +
            "LEFT JOIN FETCH ce.departement " +
            "LEFT JOIN FETCH ce.categorieEmploye " +
            "LEFT JOIN FETCH ce.rubriques " +
            "WHERE ce.employe.id = :employeId")
    Optional<ContratEmploye> findByEmployeIdWithAllRelations(@Param("employeId") Long employeId);

    @Query("SELECT ce FROM ContratEmploye ce " +
            "LEFT JOIN FETCH ce.employe e " +
            "LEFT JOIN FETCH ce.company c " +
            "LEFT JOIN FETCH ce.natureContrat nc " +
            "LEFT JOIN FETCH ce.poste p " +
            "LEFT JOIN FETCH ce.departement d " +
            "LEFT JOIN FETCH ce.categorieEmploye cat " +
            "LEFT JOIN FETCH ce.rubriques r " +
            "WHERE ce.id = :id")
    Optional<ContratEmploye> findByIdWithAllRelationsEager(@Param("id") Long id);


    @Query("SELECT c FROM ContratEmploye c WHERE c.company.id IN :companyIds AND c.arretContrat = false")
    List<ContratEmploye> findByCompanyIdsAndActifs(@Param("companyIds") List<Long> companyIds);

    @Query("SELECT c FROM ContratEmploye c WHERE c.arretContrat = false")
    List<ContratEmploye> findContratsActifs();

    @Query("SELECT c FROM ContratEmploye c WHERE c.employe.id = :employeId AND c.arretContrat = false")
    List<ContratEmploye> findContratsActifsByEmploye(@Param("employeId") Long employeId);



    // ==================== CONTRATS ARRÊTÉS ====================

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.arretContrat = true
""")
    Page<ContratEmploye> findAllContratsArretesPaginated(Pageable pageable);

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.company.id = :companyId
    AND c.arretContrat = true
""")
    Page<ContratEmploye> findContratsArretesParEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);

// ==================== CONTRATS EXPIRÉS ====================

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.date_fin < CURRENT_DATE
    AND c.type_contrat = 'CDD'
""")
    Page<ContratEmploye> findAllContratsExpiresPaginated(Pageable pageable);

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.company.id = :companyId
    AND c.date_fin < CURRENT_DATE
    AND c.type_contrat = 'CDD'
""")
    Page<ContratEmploye> findContratsExpiresParEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);

// ==================== CONTRATS ARRÊTÉS OU EXPIRÉS ====================

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.arretContrat = true
    OR (c.date_fin < CURRENT_DATE AND c.type_contrat = 'CDD')
""")
    Page<ContratEmploye> findContratsArretesOuExpiresPaginated(Pageable pageable);

    @Query("""
    SELECT c FROM ContratEmploye c
    LEFT JOIN FETCH c.employe e
    LEFT JOIN FETCH c.company comp
    LEFT JOIN FETCH c.departement d
    LEFT JOIN FETCH c.poste p
    LEFT JOIN FETCH c.categorieEmploye ce
    LEFT JOIN FETCH c.natureContrat nc
    LEFT JOIN FETCH c.modeDePaiement mdp
    LEFT JOIN FETCH c.banque b
    WHERE c.company.id = :companyId
    AND (c.arretContrat = true OR (c.date_fin < CURRENT_DATE AND c.type_contrat = 'CDD'))
""")
    Page<ContratEmploye> findContratsArretesOuExpiresParEntreprisePaginated(@Param("companyId") Long companyId, Pageable pageable);

    @Query("""
    SELECT c FROM ContratEmploye c
    WHERE  c.date_debut BETWEEN :startDate AND :endDate
""")
    List<ContratEmploye> findContratsEnCoursParMois(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
