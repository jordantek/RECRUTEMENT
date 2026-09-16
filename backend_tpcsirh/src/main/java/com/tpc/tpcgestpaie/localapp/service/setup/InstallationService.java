package com.tpc.tpcgestpaie.localapp.service.setup;

import com.tpc.tpcgestpaie.localapp.dto.setup.InstallationRequest;
import com.tpc.tpcgestpaie.localapp.dto.setup.InstallationResult;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.UserCompany;
import com.tpc.tpcgestpaie.localapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class InstallationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final PasswordEncoder passwordEncoder;

    public InstallationService(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               UserCompanyRepository userCompanyRepository,
                               RoleRepository roleRepository,
                               StatusRepository statusRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userCompanyRepository = userCompanyRepository;
        this.roleRepository = roleRepository;
        this.statusRepository = statusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Vérifie si l'installation est nécessaire
     */
    public boolean isInstallationRequired() {
        // Vérifier s'il y a des entreprises clientes
        long clientCompaniesCount = companyRepository.countByClientIsNull();
        return clientCompaniesCount == 0;
    }

    /**
     * Installation initiale du logiciel
     */
    public InstallationResult performInitialInstallation(InstallationRequest request) {
        // Vérifier que l'installation est nécessaire
        if (!isInstallationRequired()) {
            throw new RuntimeException("L'installation a déjà été effectuée");
        }

        // Valider que le nom d'entreprise n'existe pas déjà
        if (companyRepository.existsByName(request.getCompanyName())) {
            throw new RuntimeException("Une entreprise avec ce nom existe déjà");
        }

        // 1. Créer l'entreprise cliente
        Company clientCompany = createClientCompany(request);

        // 2. Créer/Mettre à jour l'utilisateur admin
        User adminUser = createOrUpdateAdminUser(request, clientCompany);

        // 3. Associer l'utilisateur à l'entreprise (table user_company)
        associateUserToCompany(adminUser, clientCompany);

        return new InstallationResult(clientCompany, adminUser, true, "Installation réussie");
    }

    /**
     * Créer l'entreprise cliente
     */
    private Company createClientCompany(InstallationRequest request) {
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setEmail(request.getCompanyEmail());
        company.setPhone(request.getCompanyPhone());
        company.setAddress(request.getCompanyAddress());
        company.setCountry(request.getCompanyCountry());
        company.setRccm(request.getRccm());
        company.setIfu(request.getIfu());
        company.setDirectorName(request.getDirectorName());
        company.setDirectorEmail(request.getDirectorEmail());
        company.setDirectorPhone(request.getDirectorPhone());

        // Entreprise cliente (client_id = NULL)
        company.setClient(null);

        // Type de licence
        if (request.getLicenseType() != null) {
            company.setLicenseType(request.getLicenseType());
        } else {
            company.setLicenseType(Company.LicenseType.MULTI_COMPANY);
        }

        company.setEstEntreprisePrincipale(true);

        // Valeurs par défaut
        company.setCreationDate(java.time.LocalDate.now());
        company.setVps(0.04);
        company.setVpsEffectDate(java.time.LocalDate.now().plusMonths(1));
        company.setTvaVal(java.math.BigDecimal.valueOf(18));
        company.setStatusId((short) 1);

        return companyRepository.save(company);
    }

    /**
     * Créer ou mettre à jour l'utilisateur admin
     */
    private User createOrUpdateAdminUser(InstallationRequest request, Company company) {
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("admin"));
        User adminUser;

        if (existingUser.isPresent()) {
            adminUser = existingUser.get();
            adminUser.setFullName(request.getAdminFullName());
            adminUser.setEmail(request.getAdminEmail());
            adminUser.setPhone(request.getAdminPhone());
            adminUser.setCompany(company); // ⭐ Associer l'entreprise
        } else {
            adminUser = createDefaultAdminUser(request, company); // ⭐ Passer company
        }

        // Mettre à jour le mot de passe
        if (request.getAdminPassword() != null && !request.getAdminPassword().isEmpty()) {
            adminUser.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        } else if (adminUser.getPassword() == null) {
            adminUser.setPassword(passwordEncoder.encode("admin123"));
        }

        return userRepository.save(adminUser);
    }

    /**
     * Créer l'utilisateur admin par défaut
     */
    private User createDefaultAdminUser(InstallationRequest request, Company company) {
        Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle SUPER_ADMIN non trouvé"));


        User user = new User();
        user.setUsername("admin");
        user.setFullName(request.getAdminFullName());
        user.setEmail(request.getAdminEmail());
        user.setPhone(request.getAdminPhone());
        user.setCompany(company); // ⭐ ASSOCIATION DE L'ENTREPRISE
        user.getRoles().add(superAdminRole);
        user.setStatus(statusRepository.findByName("active"));
        user.setPassword(passwordEncoder.encode(
                request.getAdminPassword() != null ? request.getAdminPassword() : "admin123"
        ));

        return user;
    }

    /**
     * Associer l'utilisateur à l'entreprise (table user_company)
     */
    private void associateUserToCompany(User user, Company company) {
        boolean alreadyAssociated = userCompanyRepository.findByUserAndCompanyAndRemovedAtIsNull(user, company)
                .isPresent();

        if (!alreadyAssociated) {
            UserCompany userCompany = new UserCompany();
            userCompany.setUser(user);
            userCompany.setCompany(company);
            userCompany.setAssignedAt(LocalDateTime.now());
            userCompanyRepository.save(userCompany);
        }
    }

    // ... autres méthodes
}