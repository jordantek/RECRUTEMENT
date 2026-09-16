package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.MinistereTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinistereTravailRepository extends JpaRepository<MinistereTravail, Long> {
    MinistereTravail findTopByOrderByIdAsc();
}
