package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EmployeDiplome;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmployeDiplomeRepository extends JpaRepository<EmployeDiplome, Long> {

    // Charge Employe et Diplome avec une seule requête
    @EntityGraph(attributePaths = {"employe", "diplome"})
    List<EmployeDiplome> findAll();

    // Variante pour récupérer les diplômes d'un employé précis, aussi avec fetch
    @EntityGraph(attributePaths = {"employe", "diplome"})
    List<EmployeDiplome> findByEmployeId(Long employeId);

    boolean existsByEmployeIdAndDiplomeId(Long id, Long id1);

    @Query("""
        SELECT ed 
        FROM EmployeDiplome ed 
        WHERE ed.employe = :employe 
          AND (ed.deleted_at IS NULL) 
          AND (ed.created_at <= :dateEffet)
    """)
    List<EmployeDiplome> findDiplomesActifs(@Param("employe") Employe employe,
                                            @Param("dateEffet") LocalDate dateEffet);
}
