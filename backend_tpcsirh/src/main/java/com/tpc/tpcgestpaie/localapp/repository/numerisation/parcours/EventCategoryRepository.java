package com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours;

import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EmployeeEvent;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    Optional<EventCategory> findByCode(String code);
    boolean existsByCode(String code);

    List<EventCategory> findAllByOrderByDisplayOrderAsc();

}
