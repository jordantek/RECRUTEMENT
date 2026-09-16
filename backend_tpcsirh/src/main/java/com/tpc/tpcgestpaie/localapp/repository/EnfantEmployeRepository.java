package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnfantEmployeRepository extends JpaRepository<EnfantEmploye, Long> {

    List<EnfantEmploye> findByEmployeIdAndDeletedAtIsNull(Long employeId);

    int countByEmployeIdAndDeletedAtIsNull(Long employeId);
}