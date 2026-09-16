package com.tpc.tpcgestpaie.localapp.service.users;

import com.tpc.tpcgestpaie.localapp.dto.users.UserAvecEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.users.UserFullRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.users.UserResponseWithHierarchyDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEmployeService {

    private final UserRepository userRepository;
    private final EmployeSuperieurRepository employeSuperieurRepository;
    private final UserService userService;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    /**
     * Récupère tous les utilisateurs d'une entreprise spécifique avec leurs infos employé
     */
//    public List<UserAvecEmployeDTO> getUsersByCompany(Long companyId) {
//        log.info("🔍 Récupération des utilisateurs pour l'entreprise ID: {}", companyId);
//
//        List<User> users = userRepository.findByCompanyId(companyId);
//
//        return users.stream()
//                .map(this::toUserAvecEmployeDTO)
//                .collect(Collectors.toList());
//    }

    public List<UserAvecEmployeDTO> getUsersByCompany(Long companyId) {
        log.info("🔍 Récupération des utilisateurs pour l'entreprise ID: {}", companyId);

        List<User> users = userRepository.findByCompanyId(companyId);

        return users.stream()
                .map(user -> {
                    // Récupérer la hiérarchie active pour cet employé
                    EmployeSuperieur hierarchie = employeSuperieurRepository
                            .findByContratEmployeIdAndIsActiveTrue(user.getEmploye().getId())
                            .orElse(null);

                    // Mapper vers le DTO avec hiérarchie
                    return toUserAvecEmployeDTO(user, hierarchie);
                })
                .sorted(Comparator.comparing(UserAvecEmployeDTO::getFullName))
                .toList();
    }


    /**
     * Récupère tous les utilisateurs actifs d'une entreprise
     */
    public List<UserAvecEmployeDTO> getActiveUsersByCompany(Long companyId) {
        log.info("🔍 Récupération des utilisateurs actifs pour l'entreprise ID: {}", companyId);

        List<User> users = userRepository.findByCompanyIdAndDeletedAtIsNull(companyId);

        return users.stream()
                .map(user -> toUserAvecEmployeDTOWithHierarchy(user))
                .collect(Collectors.toList());
    }

    public List<UserAvecEmployeDTO> searchUsersInCompany(Long companyId, String searchTerm) {
        log.info("🔍 Recherche d'utilisateurs dans l'entreprise {} avec le terme: {}", companyId, searchTerm);

        List<User> users = userRepository.findByCompanyIdAndSearchTerm(companyId, searchTerm.toLowerCase());

        return users.stream()
                .map(user -> toUserAvecEmployeDTOWithHierarchy(user))
                .collect(Collectors.toList());
    }


    private UserAvecEmployeDTO toUserAvecEmployeDTOWithHierarchy(User user) {
        // Récupération des infos employé
        UserAvecEmployeDTO.EmployeInfoDTO employeInfo = null;
        if (user.getEmploye() != null) {
            employeInfo = UserAvecEmployeDTO.EmployeInfoDTO.builder()
                    .id(user.getEmploye().getId())
                    .matricule(user.getEmploye().getMatricule())
                    .nom(user.getEmploye().getNom())
                    .prenom(user.getEmploye().getPrenom())
                    .telephone(user.getEmploye().getTelephone())
                    .email(user.getEmploye().getEmail())

                    .build();
        }

        // Récupération des infos entreprise
        UserAvecEmployeDTO.CompanyInfoDTO companyInfo = null;
        if (user.getCompany() != null) {
            companyInfo = UserAvecEmployeDTO.CompanyInfoDTO.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .build();
        }

        // Récupération de la hiérarchie
        EmployeSuperieur hierarchie = null;
        if (user.getEmploye() != null) {
            hierarchie = employeSuperieurRepository
                    .findByContratEmployeIdAndIsActiveTrue(user.getEmploye().getId())
                    .orElse(null);
        }

        return UserAvecEmployeDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .status(user.getStatus() != null ? user.getStatus().getName() : null)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .employe(employeInfo)
                .company(companyInfo)
                .hierarchie(hierarchie != null && hierarchie.getHierarchie() != null
                        ? hierarchie.getHierarchie().getSuperieurs()
                        : Collections.emptyList())
                .build();
    }


    /**
     * Convertit un User en DTO avec les informations de l'employé
     */
    private UserAvecEmployeDTO toUserAvecEmployeDTO(User user, EmployeSuperieur hierarchie) {

        UserAvecEmployeDTO.EmployeInfoDTO employeInfo = UserAvecEmployeDTO.EmployeInfoDTO.builder()
                .id(user.getEmploye().getId())
                .matricule(user.getEmploye().getMatricule())
                .nom(user.getEmploye().getNom())
                .prenom(user.getEmploye().getPrenom())
                .telephone(user.getEmploye().getTelephone())
                .email(user.getEmploye().getEmail())
                .build();

        UserAvecEmployeDTO.CompanyInfoDTO companyInfo = UserAvecEmployeDTO.CompanyInfoDTO.builder()
                .id(user.getCompany().getId())
                .name(user.getCompany().getName())
                .build();

        return UserAvecEmployeDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .status(user.getStatus() != null ? user.getStatus().getName() : null)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .employe(employeInfo)
                .company(companyInfo)
                .hierarchie(hierarchie != null && hierarchie.getHierarchie() != null
                        ? hierarchie.getHierarchie().getSuperieurs()
                        : Collections.emptyList())
                .build();
    }

//    private UserAvecEmployeDTO toUserAvecEmployeDTO(User user) {
//        UserAvecEmployeDTO.EmployeInfoDTO employeInfo = null;
//        if (user.getEmploye() != null) {
//            employeInfo = UserAvecEmployeDTO.EmployeInfoDTO.builder()
//                    .id(user.getEmploye().getId())
//                    .matricule(user.getEmploye().getMatricule())
//                    .nom(user.getEmploye().getNom())
//                    .prenom(user.getEmploye().getPrenom())
//                    .telephone(user.getEmploye().getTelephone())
//                    .email(user.getEmploye().getEmail())
//                    .posteActuel(getPosteActuel(user.getEmploye()))
//                    .departementActuel(getDepartementActuel(user.getEmploye()))
//                    .build();
//        }
//
//        UserAvecEmployeDTO.CompanyInfoDTO companyInfo = null;
//        if (user.getCompany() != null) {
//            companyInfo = UserAvecEmployeDTO.CompanyInfoDTO.builder()
//                    .id(user.getCompany().getId())
//                    .name(user.getCompany().getName())
//                    .build();
//        }
//
//        return UserAvecEmployeDTO.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .fullName(user.getFullName())
//                .phone(user.getPhone())
//                .roles(user.getRoleNames())
//                .status(user.getStatus() != null ? user.getStatus().getName() : "Inactif")
//                .lastLoginAt(user.getLastLoginAt())
//                .createdAt(user.getCreatedAt())
//                .employe(employeInfo)
//                .company(companyInfo)
//                .build();
//    }

    /**
     * Récupère le poste actuel de l'employé (à adapter selon votre modèle)
     */
    private String getPosteActuel(Employe employe) {
        // Implémentation à adapter selon votre modèle de données
        // Exemple: récupérer le poste du contrat actuel
        return "Poste à définir"; // Remplacez par la logique réelle
    }

    /**
     * Récupère le département actuel de l'employé (à adapter selon votre modèle)
     */
    private String getDepartementActuel(Employe employe) {
        // Implémentation à adapter selon votre modèle de données
        // Exemple: récupérer le département du contrat actuel
        return "Département à définir"; // Remplacez par la logique réelle
    }

    /**
     * Recherche des utilisateurs par nom, prénom ou email dans une entreprise
     */

    /**
     * Récupère un utilisateur avec sa hiérarchie complète
     */
    @Transactional
    public UserResponseWithHierarchyDTO getUserWithHierarchy(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Récupérer la hiérarchie si elle existe
        EmployeSuperieur employeSuperieur = employeSuperieurRepository
                .findByContratEmployeIdAndIsActiveTrue(
                        user.getEmploye().getId()
                ).orElse(null);

        return mapToResponse(user, employeSuperieur);
    }

    /**
     * Mapper pour construire le DTO
     */
    private UserResponseWithHierarchyDTO mapToResponse(User user, EmployeSuperieur employeSuperieur) {

        List<UserResponseWithHierarchyDTO.SuperieurDTO> superieursList = employeSuperieur != null && employeSuperieur.getHierarchie() != null
                ? employeSuperieur.getHierarchie().getSuperieurs().stream()
                .map(sup -> new UserResponseWithHierarchyDTO.SuperieurDTO(
                        sup.getContratSuperieurId(),
                        sup.getEmployeSuperieurId(),
                        sup.getNomComplet(),
                        sup.getFonction(),
                        sup.getDepartement(),
                        sup.getCompanyName(),
                        sup.getOrdre()
                ))
                .sorted(Comparator.comparing(UserResponseWithHierarchyDTO.SuperieurDTO::getOrdre))
                .toList()
                : List.of();

        return new UserResponseWithHierarchyDTO(
                user.getId(),
                user.getEmploye() != null ? user.getEmploye().getId() : null,
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus() != null ? user.getStatus().getName() : null,
                user.getCompany() != null ? user.getCompany().getId() : null,
                user.getCompany() != null ? user.getCompany().getName() : null,
                user.getRoles().stream().map(Role::getName).toList(),
                superieursList
        );
    }

    /**
     * Récupère tous les utilisateurs avec leur hiérarchie complète
     */
    @Transactional
    public List<UserResponseWithHierarchyDTO> getAllUsersWithHierarchy() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    EmployeSuperieur employeSuperieur = employeSuperieurRepository
                            .findByContratEmployeIdAndIsActiveTrue(user.getEmploye().getId())
                            .orElse(null);

                    return mapToResponse(user, employeSuperieur);
                })
                .sorted(Comparator.comparing(UserResponseWithHierarchyDTO::getFullName))
                .toList();
    }

    @Transactional
    public User createOrUpdateUserWithHierarchy(UserFullRequestDTO dto) {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) throw new RuntimeException("Utilisateur non authentifié");

        boolean isUpdate = dto.getUserId() != null;

        User user = isUpdate
                ? userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
                : new User();

        // --- Vérifications de doublons
        if (!isUpdate && userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        if (!isUpdate && userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email déjà utilisé");

        // --- Employé et entreprise
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));

        user.setEmploye(employe);
        user.setCompany(company);
        user.setFullName(employe.getNom() + " " + employe.getPrenom());
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());

        // --- Roles
        Set<Role> roles = new HashSet<>();
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> rolesList = roleRepository.findAllById(dto.getRoleIds());
            if (rolesList.size() != dto.getRoleIds().size())
                throw new RuntimeException("Un ou plusieurs rôles introuvables");
            roles.addAll(rolesList);
        } else {
            roleRepository.findByName("EMPLOYE").ifPresent(roles::add);
        }
        user.setRoles(roles);

        // --- Status
        Status status = statusRepository.findByNameOptional(dto.getStatus())
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        user.setStatus(status);

        // --- Save user
        if (!isUpdate) user.setAddedBy(currentUser);

        User savedUser = userRepository.save(user);

        // --- Hiérarchie
        if (dto.getSuperieurs() != null && !dto.getSuperieurs().isEmpty()) {

            Optional<ContratEmploye> contratUser =contratEmployeRepository.findActifByEmployeId(employe.getId());
            if (contratUser.isEmpty()) throw new RuntimeException("Contrat employé introuvable");

            // Vérifier doublons d'ordre
            Set<Integer> ordres = new HashSet<>();
            for (UserFullRequestDTO.SuperieurDTO s : dto.getSuperieurs()) {
                if (!ordres.add(s.getOrdre()))
                    throw new RuntimeException("Doublon d'ordre détecté : " + s.getOrdre());
            }

            HierarchieJson hierarchie = new HierarchieJson();
            List<HierarchieJson.SuperieurHierarchique> supList = new ArrayList<>();

            for (UserFullRequestDTO.SuperieurDTO s : dto.getSuperieurs()) {
                ContratEmploye contratSup = contratEmployeRepository.findById(s.getContratSuperieurId())
                        .orElseThrow(() -> new RuntimeException("Contrat supérieur introuvable : " + s.getContratSuperieurId()));

                // Vérifier entreprise
                if (!contratSup.getCompany().getId().equals(company.getId()))
                    throw new RuntimeException("Le supérieur doit appartenir à la même entreprise");

                Employe supEmp = contratSup.getEmploye();
                supList.add(new HierarchieJson.SuperieurHierarchique(
                        s.getContratSuperieurId(),
                        supEmp != null ? supEmp.getId() : null,
                        contratSup.getPoste() != null ? contratSup.getPoste().getLibelle() : "",
                        company.getName(),
                        supEmp != null ? supEmp.getNom() + " " + supEmp.getPrenom() : "",
                        contratSup.getPoste() != null && contratSup.getPoste().getDepartement() != null
                                ? contratSup.getPoste().getDepartement().getLibelle() : "",
                        s.getOrdre()
                ));
            }

            hierarchie.setSuperieurs(supList);

            Optional<EmployeSuperieur> existingOpt = employeSuperieurRepository.findByContratEmployeIdAndIsActiveTrue(contratUser.get().getId());
            EmployeSuperieur employeSuperieur;

            if (existingOpt.isPresent()) {
                employeSuperieur = existingOpt.get();
                employeSuperieur.setHierarchie(hierarchie);
                employeSuperieur.setUpdatedBy(currentUser);
            } else {
                employeSuperieur = new EmployeSuperieur();
                employeSuperieur.setCompany(company);
                employeSuperieur.setEmploye(employe);
                employeSuperieur.setContratEmploye(contratUser.get());
                employeSuperieur.setHierarchie(hierarchie);
                employeSuperieur.setIsActive(true);
                employeSuperieur.setCreatedBy(currentUser);
                employeSuperieur.setUpdatedBy(currentUser);
            }

            employeSuperieurRepository.save(employeSuperieur);
        }

        return savedUser;
    }
}