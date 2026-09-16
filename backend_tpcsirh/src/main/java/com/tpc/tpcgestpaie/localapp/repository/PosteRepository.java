package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Poste;
import com.tpc.tpcgestpaie.localapp.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosteRepository extends JpaRepository<Poste, Long> {

    // Trouver les postes par leur département
    List<Poste> findByDepartement(Departement departement);

    // Exemple de méthode pour trouver un poste par libellé
    Optional<Poste> findByLibelle(String libelle);

    boolean existsByLibelleAndDepartement_Id(String libelle, Long departementId);

}
