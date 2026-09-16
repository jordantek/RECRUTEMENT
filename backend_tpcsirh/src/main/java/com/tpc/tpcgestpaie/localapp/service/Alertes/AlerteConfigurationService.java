package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.AlerteConfigurationRequestDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.repository.alertes.AlerteConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AlerteConfigurationService {

    private final AlerteConfigurationRepository configurationRepository;

    public AlerteConfigurationService(AlerteConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Récupère la configuration pour un type d'alerte et un utilisateur
     * Si l'utilisateur n'a pas de configuration, crée une par défaut
     */
    public AlerteConfiguration getConfigurationForUser(AlerteConfiguration.TypeAlerte typeAlerte, User user) {
        Optional<AlerteConfiguration> configOpt = configurationRepository.findByUserIdAndType(user.getId(), typeAlerte);

        if (configOpt.isPresent()) {
            return configOpt.get();
        }

        // Créer une configuration par défaut pour cet utilisateur
        return createDefaultConfigurationForUser(typeAlerte, user);
    }

    /**
     * Vérifie si un type d'alerte est activé pour un utilisateur
     */
    public boolean isAlerteEnabledForUser(AlerteConfiguration.TypeAlerte typeAlerte, User user) {
        return configurationRepository.findByUserIdAndType(user.getId(), typeAlerte)
                .map(AlerteConfiguration::isEnabled)
                .orElseGet(() -> {
                    // Si pas de configuration, créer une par défaut et retourner son état
                    AlerteConfiguration defaultConfig = createDefaultConfigurationForUser(typeAlerte, user);
                    return defaultConfig.isEnabled();
                });
    }
    /**
     * Récupère toutes les configurations d'un utilisateur
     */
    public List<AlerteConfiguration> getUserConfigurations(User user) {
        List<AlerteConfiguration> userConfigs = configurationRepository.findByUserId(user.getId());

        // Si l'utilisateur n'a aucune configuration, initialiser avec les valeurs par défaut
        if (userConfigs.isEmpty()) {
            userConfigs = initializeUserConfigurations(user);
        }
        return userConfigs;
    }

    /**
     * Initialise toutes les configurations pour un nouvel utilisateur
     */
    @Transactional
    public List<AlerteConfiguration> initializeUserConfigurations(User user) {
        log.info("Initialisation des configurations d'alertes pour l'utilisateur: {}", user.getUsername());

        for (AlerteConfiguration.TypeAlerte type : AlerteConfiguration.TypeAlerte.values()) {
            createDefaultConfigurationForUser(type, user);
        }

        return configurationRepository.findByUserId(user.getId());
    }

    /**
     * Crée ou met à jour une configuration utilisateur
     */
    public AlerteConfiguration saveUserConfiguration(AlerteConfiguration config, User currentUser) {
        // S'assurer que la configuration appartient à l'utilisateur connecté
        if (!config.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Vous ne pouvez modifier que vos propres configurations");
        }

        // Validation des échéances
        validateEcheances(config.getEcheancesJours());

        return configurationRepository.save(config);
    }

    /**
     * Active/désactive une configuration utilisateur
     */
    public AlerteConfiguration toggleUserConfiguration(Long configId, User currentUser, boolean enabled) {
        Optional<AlerteConfiguration> configOpt = configurationRepository.findById(configId);
        if (configOpt.isPresent()) {
            AlerteConfiguration config = configOpt.get();

            // Vérifier que l'utilisateur est propriétaire
            if (!config.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("Accès non autorisé à cette configuration");
            }

            config.setEnabled(enabled);
            return configurationRepository.save(config);
        }
        throw new RuntimeException("Configuration non trouvée: " + configId);
    }

    /**
     * Réinitialise une configuration aux valeurs par défaut
     */
    public AlerteConfiguration resetToDefault(Long configId, User currentUser) {
        Optional<AlerteConfiguration> configOpt = configurationRepository.findById(configId);
        if (configOpt.isPresent()) {
            AlerteConfiguration config = configOpt.get();

            // Vérifier que l'utilisateur est propriétaire
            if (!config.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("Accès non autorisé à cette configuration");
            }

            AlerteConfiguration defaultConfig = createDefaultConfiguration(config.getTypeAlerte());
            config.setEcheancesJours(defaultConfig.getEcheancesJours());
            config.setNotifierJourJ(defaultConfig.isNotifierJourJ());
            config.setMotifsImportants(defaultConfig.getMotifsImportants());
            config.setEnabled(defaultConfig.isEnabled());

            return configurationRepository.save(config);
        }
        throw new RuntimeException("Configuration non trouvée: " + configId);
    }

    private AlerteConfiguration createDefaultConfigurationForUser(AlerteConfiguration.TypeAlerte typeAlerte, User user) {
        AlerteConfiguration defaultConfig = createDefaultConfiguration(typeAlerte);
        defaultConfig.setUser(user);

        return configurationRepository.save(defaultConfig);
    }

    private AlerteConfiguration createDefaultConfiguration(AlerteConfiguration.TypeAlerte typeAlerte) {
        AlerteConfiguration config = new AlerteConfiguration();
        config.setTypeAlerte(typeAlerte);
        config.setLibelle(typeAlerte.getLibelle());
        config.setEnabled(true);
        config.setNotifierJourJ(true);

        switch (typeAlerte) {
            case ANNIVERSAIRE_NAISSANCE:
                config.setEcheancesJours("7,3,1");
                config.setDescription("Alertes pour les anniversaires de naissance des employés");
                break;
            case ANNIVERSAIRE_RECRUTEMENT:
                config.setEcheancesJours("30,15,7");
                config.setDescription("Alertes pour les anniversaires de recrutement");
                break;
            case FIN_ESSAI:
                config.setEcheancesJours("15,7,3");
                config.setDescription("Alertes avant la fin des périodes d'essai");
                break;
            case FIN_CONTRAT:
                config.setEcheancesJours("30,15,7");
                config.setDescription("Alertes avant la fin des contrats");
                break;
            case DEBUT_ABSENCE:
                config.setEcheancesJours("2,1");
                config.setMotifsImportants("CONGE_MALADIE,CONGE_ANNUEL,MISSION,FORMATION");
                config.setDescription("Alertes pour les départs en absence");
                break;
            case FIN_ABSENCE:
                config.setEcheancesJours("2,1");
                config.setMotifsImportants("CONGE_MALADIE,CONGE_ANNUEL,MISSION,FORMATION");
                config.setDescription("Alertes pour les retours d'absence");
                break;
            case JOURNAL_RH:
                config.setEcheancesJours("5,3,1");
                config.setDescription("Alertes pour les événements du journal RH");
                config.setMotifsImportants("REUNION,ENTRETIEN,FORMATION,EVENEMENT,DEADLINE");
                break;
        }

        return config;
    }

    private void validateEcheances(String echeances) {
        if (echeances == null || echeances.trim().isEmpty()) {
            throw new IllegalArgumentException("Les échéances ne peuvent pas être vides");
        }

        try {
            String[] jours = echeances.split(",");
            for (String jour : jours) {
                int j = Integer.parseInt(jour.trim());
                if (j < 0) {
                    throw new IllegalArgumentException("Les échéances ne peuvent pas être négatives");
                }
                if (j > 365) {
                    throw new IllegalArgumentException("Les échéances ne peuvent pas dépasser 365 jours");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format d'échéances invalide. Utilisez: '7,3,1'");
        }
    }


    /**
     * Crée une configuration à partir d'un DTO
     */
    public AlerteConfiguration createFromDTO(AlerteConfigurationRequestDTO configDTO, User user) {
        AlerteConfiguration config = configDTO.toEntity();
        config.setUser(user);
        return saveUserConfiguration(config, user);
    }

    /**
     * Met à jour une configuration à partir d'un DTO
     */
    public AlerteConfiguration updateFromDTO(Long id, AlerteConfigurationRequestDTO configDTO, User user) {
        AlerteConfiguration config = configDTO.toEntity();
        config.setId(id);
        config.setUser(user);
        return saveUserConfiguration(config, user);
    }
}