package com.tpc.tpcgestpaie.localapp.service.users;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserCompanyAccessService {

    private final AdminEligibilityService adminEligibilityService;

    private final EmployeRepository employeRepository;

    public UserCompanyAccessService(AdminEligibilityService adminEligibilityService, EmployeRepository employeRepository) {
        this.adminEligibilityService = adminEligibilityService;
        this.employeRepository = employeRepository;
    }

    /**
     * Récupère toutes les entreprises accessibles par l'utilisateur connecté
     * Récupère toutes les entreprises accessibles par l'utilisateur connecté
     */
    /**
     * Récupère toutes les entreprises accessibles par l'utilisateur connecté
     */
    @Transactional
    public List<Company> getAccessibleCompanies(User currentUser) {
        if (currentUser == null) {
            return List.of();
        }

        // Si l'utilisateur est lié à un employé, on récupère ses entreprises via ses contrats
        if (currentUser.getEmploye() != null) {
            return employeRepository.findCompaniesByEmployeId(currentUser.getEmploye().getId());
        }

        // Si l'utilisateur a une entreprise principale
        if (currentUser.getCompany() != null) {
            Company userCompany = currentUser.getCompany();

            // Si l'utilisateur peut gérer plusieurs entreprises
            if (adminEligibilityService.canManageCompanies(currentUser) &&
                    userCompany.canManageCompanies()) {
                return userCompany.getAllAccessibleCompanies();
            }

            // Sinon, seulement son entreprise
            return List.of(userCompany);
        }

        // Si l'utilisateur n'a pas d'entreprise associée
        return List.of();
    }

    /**
     * Vérifie si l'utilisateur a accès à une entreprise spécifique
     */
    public boolean hasAccessToCompany(User currentUser, Long companyId) {
        return getAccessibleCompanies(currentUser).stream()
                .anyMatch(company -> company.getId().equals(companyId));
    }

    /**
     * Vérifie si l'utilisateur a accès à un employé spécifique (via ses contrats)
     */
    public boolean hasAccessToEmploye(User currentUser, Long employeId) {
        List<Long> accessibleCompanyIds = getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return false;
        }

        // Vérifie si l'employé a un contrat actif avec une entreprise accessible
        return accessibleCompanyIds.stream()
                .anyMatch(companyId -> employeRepository.hasActiveContractWithCompany(employeId, companyId));
    }

    /**
     * Récupère les IDs des entreprises accessibles
     */
    @Transactional
    public List<Long> getAccessibleCompanyIds(User currentUser) {
        return getAccessibleCompanies(currentUser).stream()
                .map(Company::getId)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les employés accessibles par l'utilisateur
     */
    public List<Employe> getAccessibleEmployes(User currentUser) {
        List<Long> accessibleCompanyIds = getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        return employeRepository.findByCompanyIds(accessibleCompanyIds);
    }




}