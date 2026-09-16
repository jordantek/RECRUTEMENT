package com.tpc.tpcgestpaie.localapp.service.entreprise;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CompanyTypeService {

    private final UserService userService;

    public CompanyTypeService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Détermine le type d'entreprise de l'utilisateur
     */
    public CompanyType getUserCompanyType(User user) {
        if (user.getCompany() == null) {
            return CompanyType.NO_COMPANY;
        }

        Company userCompany = user.getCompany();

        if (userCompany.isClientCompany()) {
            // Entreprise principale (client)
            if (userCompany.canManageCompanies()) {
                return CompanyType.MAIN_COMPANY_MULTI; // Peut gérer d'autres entreprises
            } else {
                return CompanyType.MAIN_COMPANY_STANDALONE; // Seulement elle-même
            }
        } else {
            // Entreprise gérée
            return CompanyType.MANAGED_COMPANY;
        }
    }

    public Long getMainCompanyId(User user) {
        if (user == null || user.getCompany() == null) {
            return null; // Aucun utilisateur ou aucune entreprise
        }

        Company userCompany = user.getCompany();

        if (userCompany.isClientCompany()) {
            // C’est déjà une entreprise principale
            return userCompany.getId();
        } else {
            // C’est une entreprise gérée → on retourne l’ID de l’entreprise principale qui la gère
           return  null;
        }
    }


    /**
     * Vérifie si l'utilisateur est dans une entreprise principale
     */
    public boolean isInMainCompany(User user) {
        CompanyType companyType = getUserCompanyType(user);
        return companyType == CompanyType.MAIN_COMPANY_MULTI ||
                companyType == CompanyType.MAIN_COMPANY_STANDALONE;
    }

    /**
     * Vérifie si l'utilisateur est dans une entreprise gérée
     */
    public boolean isInManagedCompany(User user) {
        return getUserCompanyType(user) == CompanyType.MANAGED_COMPANY;
    }

    /**
     * Vérifie si l'utilisateur peut gérer d'autres entreprises
     */
    public boolean canManageOtherCompanies(User user) {
        return getUserCompanyType(user) == CompanyType.MAIN_COMPANY_MULTI;
    }

    /**
     * Enum pour les types d'entreprise
     */
    public enum CompanyType {
        NO_COMPANY,              // Aucune entreprise
        MAIN_COMPANY_MULTI,      // Entreprise principale qui peut gérer d'autres
        MAIN_COMPANY_STANDALONE, // Entreprise principale standalone
        MANAGED_COMPANY          // Entreprise gérée
    }


    /**
     * Vérifie si l'utilisateur est dans une entreprise cliente
     */
    public boolean isUserInClientCompany(User user) {
        if (user.getCompany() == null) {
            return false;
        }
        return user.getCompany().isClientCompany();
    }

    /**
     * Récupère l'entreprise cliente de l'utilisateur
     */
    public Optional<Company> getUserClientCompany(User user) {
        if (user.getCompany() == null || !user.getCompany().isClientCompany()) {
            return Optional.empty();
        }
        return Optional.of(user.getCompany());
    }

    /**
     * Récupère le type de licence de l'entreprise de l'utilisateur
     */
    public Optional<Company.LicenseType> getUserCompanyLicenseType(User user) {
        if (user.getCompany() == null || !user.getCompany().isClientCompany()) {
            return Optional.empty();
        }
        return Optional.of(user.getCompany().getLicenseType());
    }
}