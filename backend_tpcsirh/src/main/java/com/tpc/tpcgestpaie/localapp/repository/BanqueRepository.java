package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Banque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BanqueRepository extends JpaRepository<Banque, Long> {
    boolean existsByName(String name);
    Optional<Banque> findByName(String name);
}
