package com.tpc.tpcgestpaie.localapp.service.entreprise;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.users.AdminEligibilityService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MultiTenantAccessService {

    private final UserService userService;
    private final CompanyService companyService;
    private final AdminEligibilityService adminEligibilityService;
    private final CompanyTypeService companyTypeService;

    public MultiTenantAccessService(UserService userService,
                                    CompanyService companyService,
                                    AdminEligibilityService adminEligibilityService,
                                    CompanyTypeService companyTypeService) {
        this.userService = userService;
        this.companyService = companyService;
        this.adminEligibilityService = adminEligibilityService;
        this.companyTypeService = companyTypeService;
    }

    /**
     * Récupère les entreprises VISIBLES par l'utilisateur connecté
     */
    public List<CompanyDTO> getVisibleCompaniesForCurrentUser() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        // ⭐ D'ABORD : Vérifier le type d'entreprise
        CompanyTypeService.CompanyType companyType = companyTypeService.getUserCompanyType(currentUser);

        switch (companyType) {
            case NO_COMPANY:
                return List.of();

            case MANAGED_COMPANY:
                // Employé d'entreprise gérée : ne voit AUCUNE entreprise
                return List.of();

            case MAIN_COMPANY_STANDALONE:
                // Entreprise principale standalone : ne voit que son entreprise
                return List.of(CompanyDTO.fromEntity(currentUser.getCompany()));

            case MAIN_COMPANY_MULTI:
                // Entreprise principale multi-company : logique complexe
                return getVisibleCompaniesForMainCompanyUser(currentUser);

            default:
                return List.of();
        }
    }

    /**
     * Logique pour les utilisateurs d'entreprise principale MULTI_COMPANY
     */
    private List<CompanyDTO> getVisibleCompaniesForMainCompanyUser(User user) {
        Company mainCompany = user.getCompany();

        if (adminEligibilityService.canManageCompanies(user)) {
            // COMPANY_ADMIN/SUPER_ADMIN : Son entreprise + entreprises gérées
            List<CompanyDTO> managedCompanies = companyService.getManagedCompanies(mainCompany.getId());
            CompanyDTO mainCompanyDTO = CompanyDTO.fromEntity(mainCompany);
            managedCompanies.add(0, mainCompanyDTO);
            return managedCompanies;

        } else if (adminEligibilityService.canAccessAdmin(user)) {
            // HR/MANAGER/COMPTABLE : Seulement les entreprises GÉRÉES
            return companyService.getManagedCompanies(mainCompany.getId());

        } else {
            // EMPLOYEE sans droits admin : Ne voit que son entreprise principale
            return List.of(CompanyDTO.fromEntity(mainCompany));
        }
    }

    /**
     * Vérifie si l'utilisateur peut VOIR une entreprise spécifique
     */
    public boolean canUserViewCompany(User user, Long companyId) {
        // ⭐ D'ABORD : Vérifier le type d'entreprise
        CompanyTypeService.CompanyType companyType = companyTypeService.getUserCompanyType(user);

        switch (companyType) {
            case NO_COMPANY:
            case MANAGED_COMPANY:
                return false;

            case MAIN_COMPANY_STANDALONE:
                // Peut voir seulement son entreprise
                return user.getCompany().getId().equals(companyId);

            case MAIN_COMPANY_MULTI:
                return canMainCompanyUserViewCompany(user, companyId);

            default:
                return false;
        }
    }

    private boolean canMainCompanyUserViewCompany(User user, Long companyId) {
        Company userCompany = user.getCompany();

        // Peut toujours voir son entreprise principale
        if (userCompany.getId().equals(companyId)) {
            return true;
        }

        // Vérifier les droits pour voir les entreprises gérées
        if (adminEligibilityService.canManageCompanies(user)) {
            // COMPANY_ADMIN/SUPER_ADMIN : Peut voir toutes les entreprises gérées
            List<CompanyDTO> managedCompanies = companyService.getManagedCompanies(userCompany.getId());
            return managedCompanies.stream()
                    .anyMatch(company -> company.getId().equals(companyId));
        } else if (adminEligibilityService.canAccessAdmin(user)) {
            // HR/MANAGER/COMPTABLE : Peut voir les entreprises gérées
            List<CompanyDTO> managedCompanies = companyService.getManagedCompanies(userCompany.getId());
            return managedCompanies.stream()
                    .anyMatch(company -> company.getId().equals(companyId));
        } else {
            // EMPLOYEE sans droits : Ne peut voir que son entreprise principale
            return false;
        }
    }

    /**
     * Vérifie si l'utilisateur peut ADMINISTRER
     */
    public boolean canUserAdminister(User user) {
        // ⭐ D'ABORD : Doit être dans une entreprise principale MULTI_COMPANY
        if (companyTypeService.getUserCompanyType(user) != CompanyTypeService.CompanyType.MAIN_COMPANY_MULTI) {
            return false;
        }

        // Ensuite vérifier les droits utilisateur
        return adminEligibilityService.canAccessAdmin(user);
    }

    /**
     * Vérifie si l'utilisateur peut voir les employés
     */
    public boolean canUserViewEmployees(User user, Long companyId) {
        // ⭐ D'ABORD : Vérifier le type d'entreprise et les droits de base
        if (!canUserViewCompany(user, companyId)) {
            return false;
        }

        // Ensuite vérifier les droits spécifiques pour voir les employés
        return adminEligibilityService.canAccessAdmin(user);
    }

    /**
     * Récupère le contexte complet de l'utilisateur
     */
    public Map<String, Object> getUserContext(User user) {
        Map<String, Object> context = new HashMap<>();

        CompanyTypeService.CompanyType companyType = companyTypeService.getUserCompanyType(user);

        context.put("companyType", companyType.name());
        context.put("isInMainCompany", companyTypeService.isInMainCompany(user));
        context.put("isInManagedCompany", companyTypeService.isInManagedCompany(user));
        context.put("canManageOtherCompanies", companyTypeService.canManageOtherCompanies(user));
        context.put("canAdminister", canUserAdminister(user));
        context.put("canAccessAdmin", adminEligibilityService.canAccessAdmin(user));
        context.put("visibleCompanies", getVisibleCompaniesForUser(user));

        if (user.getCompany() != null) {
            context.put("userCompany", CompanyDTO.fromEntity(user.getCompany()));
        }

        return context;
    }

    private List<CompanyDTO> getVisibleCompaniesForUser(User user) {
        // Même logique que getVisibleCompaniesForCurrentUser mais avec User en paramètre
        CompanyTypeService.CompanyType companyType = companyTypeService.getUserCompanyType(user);

        switch (companyType) {
            case NO_COMPANY:
            case MANAGED_COMPANY:
                return List.of();

            case MAIN_COMPANY_STANDALONE:
                return List.of(CompanyDTO.fromEntity(user.getCompany()));

            case MAIN_COMPANY_MULTI:
                return getVisibleCompaniesForMainCompanyUser(user);

            default:
                return List.of();
        }
    }
}