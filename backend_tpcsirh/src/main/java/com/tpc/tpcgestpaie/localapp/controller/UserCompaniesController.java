package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.AssignCompaniesRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.UserCompany;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserCompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.UserCompanyService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portefeuille")
public class UserCompaniesController {

    private final UserCompanyRepository userCompanyRepository;
    private final CompanyRepository companyRepository;
    private final UserCompanyService userCompanyService;
    private final UserRepository userRepository;

    public UserCompaniesController(UserCompanyRepository userCompanyRepository, CompanyRepository companyRepository, UserCompanyService userCompanyService, UserRepository userRepository) {
        this.userCompanyRepository = userCompanyRepository;
        this.companyRepository = companyRepository;
        this.userCompanyService = userCompanyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/affectation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assignCompanies(@RequestBody AssignCompaniesRequestDTO request) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<UserCompany> assigned = new ArrayList<>();
            List<String> alreadyAssignedNames = new ArrayList<>();

            // Vérifier l'existence de l'utilisateur
            User user = userRepository.findById(request.getUserId())
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur introuvable: " + request.getUserId(), null));
            }

            // Vérifier l'existence et l'assignation de chaque company
            for (Long companyId : request.getCompanyIds()) {
                Company company = companyRepository.findById(companyId).orElse(null);
                if (company == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Entreprise introuvable: " + companyId, null));
                }

                boolean already = userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                        .stream()
                        .anyMatch(uc -> uc.getCompany().getId().equals(companyId));

                if (already) {
                    alreadyAssignedNames.add(company.getName());
                } else {
                    UserCompany uc = new UserCompany();
                    uc.setUser(user);
                    uc.setCompany(company);
                    uc.setAssignedAt(LocalDateTime.now());
                    assigned.add(userCompanyRepository.save(uc));
                }
            }

            result.put("assigned", assigned);
            result.put("alreadyAssigned", alreadyAssignedNames);

            String message = "Companies assignées avec succès";
            if (!alreadyAssignedNames.isEmpty()) {
                message += ". Certaines entreprises étaient déjà assignées: " + String.join(", ", alreadyAssignedNames);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, message, result));

        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}/companies")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<CompanyDTO>>> getUserCompanies(@PathVariable Long userId) {
        // Vérification existence utilisateur
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Utilisateur introuvable: " + userId, null));
        }

        // CORRECTION : Utilisation de la méthode fromEntity du DTO
        List<CompanyDTO> companies = userCompanyRepository.findByUserAndRemovedAtIsNull(user)
                .stream()
                .map(uc -> CompanyDTO.fromEntity(uc.getCompany())) // ✅ Correction ici
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des entreprises de l'utilisateur", companies));
    }

    @PostMapping("/desaffectation")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> unassignCompanies(@RequestBody AssignCompaniesRequestDTO request) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<String> removedCompanies = new ArrayList<>();
            List<String> notAssignedCompanies = new ArrayList<>();

            // Vérifier existence utilisateur
            User user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur introuvable: " + request.getUserId(), null));
            }

            for (Long companyId : request.getCompanyIds()) {
                // CORRECTION : Vérifier existence company d'abord
                Company company = companyRepository.findById(companyId).orElse(null);
                if (company == null) {
                    notAssignedCompanies.add("Entreprise ID " + companyId + " (introuvable)");
                    continue;
                }

                UserCompany uc = userCompanyRepository.findByUserAndCompanyAndRemovedAtIsNull(user, company)
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

            String message = "Désaffectation terminée";
            if (!notAssignedCompanies.isEmpty()) {
                message += ". Certaines entreprises n'étaient pas assignées: " + String.join(", ", notAssignedCompanies);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, message, result));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la désaffectation", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}