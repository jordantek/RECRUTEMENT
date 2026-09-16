package com.tpc.tpcgestpaie.localapp.repository.license;

import com.tpc.tpcgestpaie.localapp.model.license.LicenseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LicenseTokenRepository extends JpaRepository<LicenseToken, Long> {

    @Query("SELECT lt FROM LicenseToken lt ORDER BY lt.createdAt DESC LIMIT 1")
    Optional<LicenseToken> findLatestToken();

    @Query("SELECT COUNT(lt) > 0 FROM LicenseToken lt")
    boolean existsAnyToken();
}