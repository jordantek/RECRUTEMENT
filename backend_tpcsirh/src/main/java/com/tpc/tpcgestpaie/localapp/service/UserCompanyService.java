package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.AssignCompaniesRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.UserWithCompaniesDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.UserCompany;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserCompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserCompanyService {

    private final UserCompanyRepository userCompanyRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // CORRECTION : Supprimer le doublon entrepriseRepository
    public UserCompanyService(UserCompanyRepository userCompanyRepository,
                              UserRepository userRepository,
                              CompanyRepository companyRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public List<UserCompany> assignCompanies(AssignCompaniesRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<Company> companies = companyRepository.findAllById(request.getCompanyIds());

        // Vérifie que toutes les companies existent
        if (companies.size() != request.getCompanyIds().size()) {
            throw new RuntimeException("Certaines entreprises n'existent pas");
        }

        List<UserCompany> assigned = new ArrayList<>();

        for (Company company : companies) {
            boolean alreadyAssigned = userCompanyRepository
                    .findByUserAndRemovedAtIsNull(user)
                    .stream()
                    .anyMatch(uc -> uc.getCompany().getId().equals(company.getId()));

            if (!alreadyAssigned) {
                UserCompany uc = new UserCompany();
                uc.setUser(user);
                uc.setCompany(company);
                uc.setAssignedAt(LocalDateTime.now());
                assigned.add(userCompanyRepository.save(uc));
            }
        }
        return assigned;
    }

    @Transactional(readOnly = true)
    public List<Company> getActiveCompaniesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                .stream()
                .map(UserCompany::getCompany)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> getActiveCompaniesDTOForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                .stream()
                .map(uc -> CompanyDTO.fromEntity(uc.getCompany())) // ✅ Correction ici
                .toList();
    }

    @Transactional
    public void removeCompany(Long userId, Long companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        UserCompany uc = userCompanyRepository
                .findByUserAndRemovedAtIsNull(user)
                .stream()
                .filter(e -> e.getCompany().getId().equals(companyId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Affectation introuvable"));

        uc.setRemovedAt(LocalDateTime.now());
        userCompanyRepository.save(uc);
    }

    @Transactional(readOnly = true)
    public UserWithCompaniesDTO getUserWithCompanies(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupère tous les noms de rôles de l'utilisateur
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        boolean isAdmin = roles.contains("ROLE_COMPANY_ADMIN") || roles.contains("ROLE_SUPER_ADMIN");

        List<CompanyDTO> companies;
        if (isAdmin) {
            // CORRECTION : Utilisation de CompanyDTO.fromEntity()
            companies = companyRepository.findAll()
                    .stream()
                    .map(CompanyDTO::fromEntity) // ✅ Correction ici
                    .collect(Collectors.toList());
        } else {
            // CORRECTION : Utilisation de CompanyDTO.fromEntity()
            companies = userCompanyRepository.findByUserId(userId)
                    .stream()
                    .map(uc -> CompanyDTO.fromEntity(uc.getCompany())) // ✅ Correction ici
                    .collect(Collectors.toList());
        }

        return new UserWithCompaniesDTO(
                user.getId(),
                user.getUsername(),
                String.join(",", roles),
                companies
        );
    }

    // CORRECTION : Méthode pour désaffecter plusieurs entreprises
    @Transactional
    public Map<String, Object> unassignCompanies(AssignCompaniesRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Map<String, Object> result = new java.util.HashMap<>();
        List<String> removedCompanies = new ArrayList<>();
        List<String> notAssignedCompanies = new ArrayList<>();

        for (Long companyId : request.getCompanyIds()) {
            Company company = companyRepository.findById(companyId).orElse(null);
            if (company == null) {
                notAssignedCompanies.add("Entreprise ID " + companyId + " (introuvable)");
                continue;
            }

            UserCompany uc = userCompanyRepository
                    .findByUserAndCompanyAndRemovedAtIsNull(user, company)
                    .orElse(null);

            if (uc != null) {
                uc.setRemovedAt(LocalDateTime.now());
                userCompanyRepository.save(uc);
                removedCompanies.add(uc.getCompany().getName());
            } else {
                notAssignedCompanies.add(company.getName());
            }
        }

        result.put("removed", removedCompanies);
        result.put("notAssigned", notAssignedCompanies);

        return result;
    }

    // CORRECTION : Méthode utilitaire pour vérifier les affectations
    @Transactional(readOnly = true)
    public boolean isCompanyAssignedToUser(Long userId, Long companyId) {
        return userCompanyRepository.findByUserAndRemovedAtIsNull(
                        userRepository.findById(userId).orElse(null))
                .stream()
                .anyMatch(uc -> uc.getCompany().getId().equals(companyId));
    }
}