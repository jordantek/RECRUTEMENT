package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT s FROM Status s WHERE s.name = :name")
    Optional<Status> findByNameOptional(@Param("name") String name);
}
