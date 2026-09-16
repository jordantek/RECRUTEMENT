package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface    UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
