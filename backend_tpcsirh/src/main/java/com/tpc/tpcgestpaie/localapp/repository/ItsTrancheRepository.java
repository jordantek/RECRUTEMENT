package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.ItsTranche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItsTrancheRepository extends JpaRepository<ItsTranche, Long> {
    Optional<ItsTranche> findByUniqueKey(String uniqueKey);

    boolean existsByUniqueKey(String uniqueKey);

}
