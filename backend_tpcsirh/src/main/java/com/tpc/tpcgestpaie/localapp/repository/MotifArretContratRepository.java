
package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.MotifArretContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotifArretContratRepository extends JpaRepository<MotifArretContrat, Long> {
    boolean existsByLibelle(String libelle);  // <--- ajout

}
