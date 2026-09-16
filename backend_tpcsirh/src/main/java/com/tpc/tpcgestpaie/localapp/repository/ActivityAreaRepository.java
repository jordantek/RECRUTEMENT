package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ActivityArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ActivityAreaRepository extends JpaRepository<ActivityArea, Integer> {
    boolean existsByName(String name);

    Optional<ActivityArea> findByNameIgnoreCase(String name);

    // Ajouter cette méthode pour la recherche paginée
    Page<ActivityArea> findAll(Pageable pageable);

    List<ActivityArea> findByNameIn(Set<String> names);

    Optional<ActivityArea> findByName(String name);
}

