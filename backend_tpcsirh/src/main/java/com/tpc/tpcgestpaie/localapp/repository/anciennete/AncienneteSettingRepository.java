package com.tpc.tpcgestpaie.localapp.repository.anciennete;

import com.tpc.tpcgestpaie.localapp.model.anciennete.AncienneteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AncienneteSettingRepository extends JpaRepository<AncienneteSetting, Long> {

    @Query("""
            SELECT a
            FROM AncienneteSetting a
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE c.id = :companyId
            """)
    Optional<AncienneteSetting> findByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT a
            FROM AncienneteSetting a
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE a.company.id = :companyId AND a.deleted_at IS NULL
            """)
    Optional<AncienneteSetting> findActiveByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT a
            FROM AncienneteSetting a
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            WHERE c.id = :companyId AND a.payeAnciennete = true
            """)
    Optional<AncienneteSetting> findActiveAncienneteByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) > 0 FROM AncienneteSetting a WHERE a.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT a
            FROM AncienneteSetting a
            LEFT JOIN FETCH a.company c
            LEFT JOIN FETCH a.added_by u
            ORDER BY c.name, a.created_at DESC
            """)
    List<AncienneteSetting> findAllWithDetails();
}