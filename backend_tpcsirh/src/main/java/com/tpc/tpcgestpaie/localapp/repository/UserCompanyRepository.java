
package com.tpc.tpcgestpaie.localapp.repository;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
    List<UserCompany> findByUserAndRemovedAtIsNull(User user);

    Optional<UserCompany> findByUserAndCompanyAndRemovedAtIsNull(User user, Company company);

    List<UserCompany> findByUserId(Long userId);

}
