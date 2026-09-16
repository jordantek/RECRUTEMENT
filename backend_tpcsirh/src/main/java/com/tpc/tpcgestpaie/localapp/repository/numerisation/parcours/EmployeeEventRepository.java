package com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours;

import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EmployeeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeEventRepository extends JpaRepository<EmployeeEvent, Long> {

    // ✅ CORRIGÉ: employeId (pas employeID)
    List<EmployeeEvent> findByEmployeIdOrderByEventDateAsc(Long employeId);

    // ✅ CORRIGÉ: EmployeId (pas EmployeID)
    List<EmployeeEvent> findByEmployeIdAndCategoryIdOrderByEventDateAsc(
            Long employeId,
            Long categoryId
    );
}