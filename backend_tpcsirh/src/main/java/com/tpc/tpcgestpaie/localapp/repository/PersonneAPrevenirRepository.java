package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonneAPrevenirRepository extends JpaRepository<PersonneAPrevenir, Long> {

    public List<PersonneAPrevenir> findByEmployeIdAndDeletedAtIsNull(Long employeId);

}
