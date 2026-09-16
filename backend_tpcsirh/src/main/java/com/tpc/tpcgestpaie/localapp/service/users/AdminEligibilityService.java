package com.tpc.tpcgestpaie.localapp.service.users;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.Role;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminEligibilityService {

    // Rôles qui ont accès COMPLET à l'administration
    private final Set<String> FULL_ADMIN_ROLES = Set.of(
            "ROLE_SUPER_ADMIN",
            "ROLE_COMPANY_ADMIN"
    );

    // Rôles qui ont accès LIMITÉ à l'administration
    private final Set<String> LIMITED_ADMIN_ROLES = Set.of(
            "ROLE_HR",
            "ROLE_MANAGER",
            "COMPTABLE"
    );

    // Rôles qui n'ont PAS accès à l'administration
    private final Set<String> NON_ADMIN_ROLES = Set.of(
            "ROLE_EMPLOYEE"
    );

    /**
     * Vérifie si l'utilisateur peut accéder à l'administration
     */
    public boolean canAccessAdmin(User user) {
        Set<String> userRoles = getUserRoleNames(user);

        // Si l'utilisateur a AU MOINS un rôle admin (complet ou limité)
        return userRoles.stream()
                .anyMatch(role -> FULL_ADMIN_ROLES.contains(role) || LIMITED_ADMIN_ROLES.contains(role));
    }

    /**
     * Vérifie si l'utilisateur a l'administration COMPLÈTE
     */
    public boolean hasFullAdminAccess(User user) {
        Set<String> userRoles = getUserRoleNames(user);
        return userRoles.stream().anyMatch(FULL_ADMIN_ROLES::contains);
    }

    /**
     * Vérifie si l'utilisateur a l'administration LIMITÉE
     */
    public boolean hasLimitedAdminAccess(User user) {
        Set<String> userRoles = getUserRoleNames(user);
        return userRoles.stream().anyMatch(LIMITED_ADMIN_ROLES::contains);
    }

    /**
     * Vérifie si l'utilisateur peut gérer les entreprises
     */
    public boolean canManageCompanies(User user) {
        Set<String> userRoles = getUserRoleNames(user);
        // Seuls les SUPER_ADMIN et COMPANY_ADMIN peuvent gérer les entreprises
        return userRoles.stream().anyMatch(role ->
                "ROLE_SUPER_ADMIN".equals(role) || "ROLE_COMPANY_ADMIN".equals(role));
    }

    /**
     * Vérifie si l'utilisateur peut gérer les utilisateurs
     */
    public boolean canManageUsers(User user) {
        Set<String> userRoles = getUserRoleNames(user);
        // SUPER_ADMIN, COMPANY_ADMIN et HR peuvent gérer les users
        return userRoles.stream().anyMatch(role ->
                "ROLE_SUPER_ADMIN".equals(role) ||
                        "ROLE_COMPANY_ADMIN".equals(role) ||
                        "ROLE_HR".equals(role));
    }

    /**
     * Vérifie si l'utilisateur est un simple employé (espace employé seulement)
     */
    public boolean isRegularEmployee(User user) {
        Set<String> userRoles = getUserRoleNames(user);
        return userRoles.stream().anyMatch(NON_ADMIN_ROLES::contains) &&
                !canAccessAdmin(user);
    }

    /**
     * Retourne le niveau d'administration
     */
    public AdminLevel getAdminLevel(User user) {
        if (hasFullAdminAccess(user)) {
            return AdminLevel.FULL;
        } else if (hasLimitedAdminAccess(user)) {
            return AdminLevel.LIMITED;
        } else {
            return AdminLevel.NONE;
        }
    }

    /**
     * Récupère les noms des rôles de l'utilisateur
     */
    private Set<String> getUserRoleNames(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Enum pour les niveaux d'administration
     */
    public enum AdminLevel {
        FULL,       // ROLE_SUPER_ADMIN, ROLE_COMPANY_ADMIN
        LIMITED,    // ROLE_HR, ROLE_MANAGER
        NONE        // ROLE_EMPLOYEE, COMPTABLE
    }


    /**
     * Vérifie si l'utilisateur est dans une entreprise principale ET a les droits d'admin complet
     */
    public boolean isFullAdminInMainCompany(User user) {
        if (user == null) {
            return false;
        }
        // Vérifier si l'utilisateur a les droits d'admin complet
        boolean hasFullAdmin = hasFullAdminAccess(user);
        if (!hasFullAdmin) {
            return false;
        }
        // Vérifier si l'utilisateur est dans une entreprise principale
        Company userCompany = user.getCompany();
        if (userCompany == null) {
            return false;
        }

        // Une entreprise principale a client_id = null ET estEntreprisePrincipale = true
        boolean isMainCompany = userCompany.getClient() == null &&
                Boolean.TRUE.equals(userCompany.getEstEntreprisePrincipale());

        return isMainCompany;
    }

    /**
     * Vérifie si l'utilisateur peut gérer les entreprises principales
     * (Super Admin dans l'entreprise principale)
     */
    public boolean canManageMainCompanies(User user) {
        if (!isFullAdminInMainCompany(user)) {
            return false;
        }
        // Seuls les SUPER_ADMIN dans l'entreprise principale peuvent gérer les entreprises principales
        Set<String> userRoles = getUserRoleNames(user);
        return userRoles.stream().anyMatch("ROLE_SUPER_ADMIN"::equals);
    }

    /**
     * Vérifie si l'utilisateur peut gérer les entreprises gérées
     * (Company Admin dans l'entreprise principale)
     */
    public boolean canManageManagedCompanies(User user) {
        if (!isFullAdminInMainCompany(user)) {
            return false;
        }

        Set<String> userRoles = getUserRoleNames(user);
        return userRoles.stream().anyMatch(role ->
                "ROLE_SUPER_ADMIN".equals(role) || "ROLE_COMPANY_ADMIN".equals(role));
    }

    /**
     * Retourne le type d'entreprise de l'utilisateur
     */
    public CompanyType getUserCompanyType(User user) {
        if (user == null || user.getCompany() == null) {
            return CompanyType.NONE;
        }

        Company company = user.getCompany();

        if (company.getClient() == null && Boolean.TRUE.equals(company.getEstEntreprisePrincipale())) {
            return CompanyType.MAIN_COMPANY;
        } else if (company.getClient() != null) {
            return CompanyType.MANAGED_COMPANY;
        } else {
            return CompanyType.STANDALONE_COMPANY;
        }
    }

    /**
     * Enum pour les types d'entreprise
     */
    public enum CompanyType {
        MAIN_COMPANY,       // Entreprise principale (client_id = null, estEntreprisePrincipale = true)
        MANAGED_COMPANY,    // Entreprise gérée (client_id != null)
        STANDALONE_COMPANY, // Entreprise standalone (client_id = null, estEntreprisePrincipale = false)
        NONE                // Aucune entreprise
    }
}