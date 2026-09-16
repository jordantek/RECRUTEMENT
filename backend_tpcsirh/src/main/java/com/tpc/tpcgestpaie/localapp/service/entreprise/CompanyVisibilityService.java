package com.tpc.tpcgestpaie.localapp.service.entreprise;


import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.UserCompany;
import com.tpc.tpcgestpaie.localapp.repository.UserCompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.users.AdminEligibilityService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class CompanyVisibilityService {

    private final UserService userService;
    private final CompanyService companyService;
    private final UserCompanyRepository userCompanyRepository;
    private final CompanyTypeService companyTypeService;
    private final AdminEligibilityService adminEligibilityService;

    public CompanyVisibilityService(UserService userService,
                                    CompanyService companyService,
                                    UserCompanyRepository userCompanyRepository,
                                    CompanyTypeService companyTypeService,
                                    AdminEligibilityService adminEligibilityService) {
        this.userService = userService;
        this.companyService = companyService;
        this.userCompanyRepository = userCompanyRepository;
        this.companyTypeService = companyTypeService;
        this.adminEligibilityService = adminEligibilityService;
    }

    /**
     * Récupère les entreprises VISIBLES selon le rôle et les assignations
     */
    @Transactional
    public List<CompanyDTO> getVisibleCompaniesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }

        // ⭐ D'ABORD : Vérifier le rôle de l'utilisateur
        if (adminEligibilityService.isRegularEmployee(currentUser)) {
            // EMPLOYEE : Ne voit AUCUNE entreprise
            return List.of();
        }

        if (currentUser.hasRole("ROLE_HR") || currentUser.hasRole("COMPTABLE")) {
            // ⭐ RH & COMPTABLE : Seulement leurs entreprises ASSIGNÉES
            return getAssignedCompaniesForUser(currentUser);
        }

        // ⭐ SUPER_ADMIN, COMPANY_ADMIN, MANAGER : Logique complexe
        return getAdminVisibleCompanies(currentUser);
    }

    /**
     * Récupère les IDs des entreprises visibles
     */
    @Transactional
    public List<Long> getVisibleCompanyIdsForCurrentUser() {
        return getVisibleCompaniesForCurrentUser()
                .stream()
                .map(CompanyDTO::getId)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si l'utilisateur peut accéder à au moins une entreprise
     */
    public boolean hasVisibleCompanies() {
        return !getVisibleCompaniesForCurrentUser().isEmpty();
    }

    /**
     * Entreprises assignées via UserCompany (pour RH et COMPTABLE)
     */
    @Transactional
    public List<CompanyDTO> getAssignedCompaniesForUser(User user) {
        return userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                .stream()
                .map(UserCompany::getCompany)
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Logique pour les administrateurs (SUPER_ADMIN, COMPANY_ADMIN, MANAGER)
     */
    @Transactional
    public List<CompanyDTO> getAdminVisibleCompanies(User user) {
        // ⭐ D'ABORD : Vérifier le type d'entreprise
        CompanyTypeService.CompanyType companyType = companyTypeService.getUserCompanyType(user);

        switch (companyType) {
            case NO_COMPANY:
                return List.of();

            case MANAGED_COMPANY:
                // Même en étant MANAGER dans une entreprise gérée, ne voit que ses assignations
                return getAssignedCompaniesForUser(user);

            case MAIN_COMPANY_STANDALONE:
                // Entreprise principale standalone : ne voit que son entreprise
                return List.of(CompanyDTO.fromEntity(user.getCompany()));

            case MAIN_COMPANY_MULTI:
                // Entreprise principale multi-company : logique selon rôle
                return getMainCompanyMultiVisibleCompanies(user);

            default:
                return List.of();
        }
    }

    /**
     * Logique pour les entreprises principales MULTI_COMPANY
     */
    @Transactional
    public List<CompanyDTO> getMainCompanyMultiVisibleCompanies(User user) {
        Company mainCompany = user.getCompany();
        List<CompanyDTO> result = new ArrayList<>();

        if (adminEligibilityService.canManageCompanies(user)) {
            // ⭐ SUPER_ADMIN & COMPANY_ADMIN : Entreprise principale + entreprises gérées
            result.add(CompanyDTO.fromEntity(mainCompany));
            result.addAll(companyService.getManagedCompanies(mainCompany.getId()));

        } else if (adminEligibilityService.hasLimitedAdminAccess(user)) {
            // ⭐ MANAGER : Entreprises assignées + entreprises gérées (sauf entreprise principale)
            List<CompanyDTO> assignedCompanies = getAssignedCompaniesForUser(user);
            List<CompanyDTO> managedCompanies = companyService.getManagedCompanies(mainCompany.getId());

            // Fusionner sans doublons
            Set<Long> companyIds = new HashSet<>();
            List<CompanyDTO> allCompanies = new ArrayList<>();

            for (CompanyDTO company : assignedCompanies) {
                if (companyIds.add(company.getId())) {
                    allCompanies.add(company);
                }
            }
            for (CompanyDTO company : managedCompanies) {
                if (companyIds.add(company.getId())) {
                    allCompanies.add(company);
                }
            }
            result.addAll(allCompanies);
        }

        return result;
    }

    /**
     * Vérifie si l'utilisateur peut VOIR une entreprise spécifique
     */
    @Transactional
    public boolean canUserViewCompany(User user, Long companyId) {
        if (adminEligibilityService.isRegularEmployee(user)) {
            return false;
        }

        if (user.hasRole("ROLE_HR") || user.hasRole("COMPTABLE")) {
            // Vérifier si l'entreprise est assignée
            return userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                    .stream()
                    .anyMatch(uc -> uc.getCompany().getId().equals(companyId));
        }

        // Pour les administrateurs
        List<CompanyDTO> visibleCompanies = getAdminVisibleCompanies(user);
        return visibleCompanies.stream()
                .anyMatch(company -> company.getId().equals(companyId));
    }

    /**
     * Récupère le contexte complet de visibilité
     */
    @Transactional
    public Map<String, Object> getVisibilityContext(User user) {
        Map<String, Object> context = new HashMap<>();

        context.put("userRoles", user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));

        context.put("companyType", companyTypeService.getUserCompanyType(user).name());
        context.put("isRegularEmployee", adminEligibilityService.isRegularEmployee(user));
        context.put("isHR", user.hasRole("ROLE_HR"));
        context.put("isComptable", user.hasRole("COMPTABLE"));
        context.put("canManageCompanies", adminEligibilityService.canManageCompanies(user));
        context.put("hasLimitedAdminAccess", adminEligibilityService.hasLimitedAdminAccess(user));

        context.put("assignedCompanies", getAssignedCompaniesForUser(user));
        context.put("visibleCompanies", getVisibleCompaniesForUser(user));

        return context;
    }


    @Transactional
    public List<CompanyDTO> getVisibleCompaniesForUser(User user) {
        // Même logique que getVisibleCompaniesForCurrentUser mais avec User en paramètre
        if (adminEligibilityService.isRegularEmployee(user)) {
            return List.of();
        }

        if (user.hasRole("ROLE_HR") || user.hasRole("COMPTABLE")) {
            return getAssignedCompaniesForUser(user);
        }

        return getAdminVisibleCompanies(user);
    }
}