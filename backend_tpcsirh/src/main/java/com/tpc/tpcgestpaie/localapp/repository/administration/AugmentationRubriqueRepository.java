package com.tpc.tpcgestpaie.localapp.repository.administration;

import com.tpc.tpcgestpaie.localapp.model.AugmentationRubrique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AugmentationRubriqueRepository extends JpaRepository<AugmentationRubrique, Long> {

    // Récupérer toutes les rubriques liées à une augmentation salariale
    List<AugmentationRubrique> findByAugmentationId(Long augmentationId);

}