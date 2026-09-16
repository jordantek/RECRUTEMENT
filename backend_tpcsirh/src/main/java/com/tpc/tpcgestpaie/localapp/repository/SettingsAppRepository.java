package com.tpc.tpcgestpaie.localapp.repository;
import com.tpc.tpcgestpaie.localapp.model.SettingsApp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SettingsAppRepository extends JpaRepository<SettingsApp, Long> {

    Optional<SettingsApp> findByCompanyId(Long companyId);

    Optional<SettingsApp> findFirstByOrderByIdAsc();
}