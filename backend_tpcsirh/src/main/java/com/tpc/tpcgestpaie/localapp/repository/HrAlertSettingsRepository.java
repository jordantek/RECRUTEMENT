package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.HrAlertSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HrAlertSettingsRepository extends JpaRepository<HrAlertSettings, Long> {

    Optional<HrAlertSettings> findFirstByCompanyIdAndUserIdAndDeletedAtIsNull(
            Long companyId,
            Long userId
    );

    Optional<HrAlertSettings> findFirstByCompanyIdAndUserIdIsNullAndDeletedAtIsNull(
            Long companyId
    );

    Optional<HrAlertSettings> findFirstByCompanyIdIsNullAndUserIdIsNullAndDeletedAtIsNull();

    Optional<HrAlertSettings> findFirstByUserIdAndDeletedAtIsNull(Long userId);

    Optional<HrAlertSettings> findFirstByUserIdAndCompanyIdAndDeletedAtIsNull(Long userId, Long companyId);

    List<HrAlertSettings> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
