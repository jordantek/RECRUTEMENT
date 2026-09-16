package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Tva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface TvaRepository extends JpaRepository<Tva, Integer> {
    // JpaRepository already provides basic CRUD operations
    // You can add custom query methods here if needed
    // For example, if you want to find Tva by taux:
    // Optional<Tva> findByTaux(Double taux);

    boolean existsByRate(BigDecimal rate);

}
