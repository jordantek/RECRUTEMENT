package com.tpc.tpcgestpaie.localapp.repository.stc;

import com.tpc.tpcgestpaie.localapp.model.IndemniteLicenciement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndemniteResumeRepository extends JpaRepository<IndemniteLicenciement, Long> {

}
