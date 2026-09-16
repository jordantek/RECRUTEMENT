package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.EmployeUserInfoDTO;
import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class UserService  {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsersWithStatus().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String username;

        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String) {
            username = principal.toString();
        } else {
            throw new RuntimeException("Type de principal non supporté");
        }

        return userRepository.findByUsername(username);
    }


    public UserDTO getCurrentUserDTO() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails currentUser_ =  (UserDetails) principal;
        String username = currentUser_.getUsername();

        return userRepository.findByUsernameWithRelations(username)
                .map(UserDTO::new)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }


    @Transactional
    public Optional<User> findById(Long addedById) {
        return userRepository.findById(addedById);
    }

    @Transactional
    public EmployeUserInfoDTO getEmployeInfoByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return null; // on peut gérer dans le contrôleur
        }
        User user = userOptional.get();
        if (user.getEmploye() == null) {
            return null; // pas d'employé associé
        }

        // Construire le DTO avec toutes les infos souhaitées
        return new EmployeUserInfoDTO(
                user.getEmploye().getId(),
                user.getEmploye().getNom(),
                user.getEmploye().getPrenom(),
                user.getEmploye().getMatricule(),
                user.getEmploye().getEmail(),
                user.getEmploye().getTelephone() // adapter selon tes champs
        );
    }

    @Transactional
    public User assignRoleToEmploye(Long employeId, Long roleId) {
        // Récupérer le user lié à l’employé
        User user = userRepository.findByEmployeId(employeId)
                .orElseThrow(() -> new RuntimeException("Aucun user trouvé pour cet employé"));
        // Récupérer le rôle
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));

        // Ajouter le rôle au set de l’utilisateur
        user.getRoles().add(role);
        // Sauvegarder
        return userRepository.save(user);
    }
}
