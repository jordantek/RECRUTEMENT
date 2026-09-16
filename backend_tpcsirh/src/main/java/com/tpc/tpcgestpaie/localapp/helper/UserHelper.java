package com.tpc.tpcgestpaie.localapp.helper;

import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Random;

@Component
public class UserHelper {

    private final UserRepository userRepository;

    // Injection des repositories pour accéder aux tables
    public UserHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUsername(String baseName, int digitCount) {
        String username;
        do {
            String randomNumber = generateRandomDigits(digitCount);
            username = baseName.toLowerCase() + "_" + randomNumber;
        } while (userRepository.findByUsername(username) != null);
        return username;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Génère un chiffre entre 0 et 9
        }
        return sb.toString();
    }
    // Vérifier si un rôle existe dans la table "roles"
 /*   public boolean isValidRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        return role != null;
    }*/

    public boolean isEmailTaken(String email) {
        try {
            return userRepository.findByEmail(email).isPresent();
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
            return true; // On considère l'email comme "pris" en cas d'erreur pour éviter les doublons
        }
    }

    // Vérifier si l'email est valide et non déjà utilisé
    public boolean isValidEmail(String email) {
        return !StringUtils.hasText(email) || isEmailTaken(email);
    }

    public boolean isValidFullName(String fullName) {
        return !StringUtils.hasText(fullName);
    }

    // Méthode pour valider un mot de passe
    public boolean isValidPassword(String password) {
        return !StringUtils.hasText(password) || password.length() < 6;
    }
    //Méthode pour valider un rôle
    public boolean isValidRole(String roleName) {
        return GlobalEnums.RoleName.isValidRole(roleName);
    }
}
