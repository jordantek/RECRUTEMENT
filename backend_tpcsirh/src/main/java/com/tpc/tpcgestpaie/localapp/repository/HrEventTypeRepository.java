package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.HrEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HrEventTypeRepository extends JpaRepository<HrEventType, Long> {
    boolean existsBySlug(String slug);

    Optional<HrEventType> findBySlug(String slug);
}
