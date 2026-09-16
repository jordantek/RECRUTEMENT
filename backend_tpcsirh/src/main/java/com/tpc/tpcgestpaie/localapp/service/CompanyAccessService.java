package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CompanyAccessService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company getClientCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée: " + companyId));

        return company.isClientCompany() ? company : company.getClient();
    }

    public List<Company> getAccessibleCompanies(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée: " + companyId));

        return company.getAllAccessibleCompanies();
    }

    public List<Long> getAccessibleCompanyIds(Long companyId) {
        return getAccessibleCompanies(companyId).stream()
                .map(Company::getId)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean hasAccessToCompany(Long currentCompanyId, Long targetCompanyId) {
        List<Long> accessibleIds = getAccessibleCompanyIds(currentCompanyId);
        return accessibleIds.contains(targetCompanyId);
    }
}