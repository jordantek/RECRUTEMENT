package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.event.HrAlertSettingsResponsDTO;
import com.tpc.tpcgestpaie.localapp.model.HrAlertSettings;
import com.tpc.tpcgestpaie.localapp.repository.HrAlertSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrAlertSettingsService {

    private final HrAlertSettingsRepository repository;

    // =========================
    // 🔍 Résolution intelligente des paramètres
    // =========================
    public HrAlertSettingsResponsDTO resolve(Long companyId, Long userId) {

        // 1️⃣ Config spécifique à l'utilisateur
        Optional<HrAlertSettings> userSettings = repository
                .findFirstByUserIdAndCompanyIdAndDeletedAtIsNull(userId, companyId);

        if (userSettings.isPresent()) {
            return toDto(userSettings.get());
        }


        // 2️⃣ Config spécifique à l'entreprise
        Optional<HrAlertSettings> companySettings = repository
                .findFirstByCompanyIdAndUserIdIsNullAndDeletedAtIsNull(companyId);

        if (companySettings.isPresent()) {
            return toDto(companySettings.get());
        }

        // 3️⃣ Config globale
        Optional<HrAlertSettings> globalSettings = repository
                .findFirstByCompanyIdIsNullAndUserIdIsNullAndDeletedAtIsNull();

        if (globalSettings.isPresent()) {
            return toDto(globalSettings.get());
        }

        // 4️⃣ Par défaut si rien trouvé
        return toDto(defaultSettings());
    }


    // =========================
    // ➕ Création ou mise à jour automatique pour éviter doublon
    // =========================
    public HrAlertSettingsResponsDTO saveOrUpdate(
            Long companyId,
            Long userId,
            Integer upcomingEventsDays,
            Integer requiredActionsDays,
            Boolean isEnabled
    ) {
        Optional<HrAlertSettings> existingOpt = repository.findFirstByCompanyIdAndUserIdAndDeletedAtIsNull(companyId, userId);

        HrAlertSettings entity;
        if (existingOpt.isPresent()) {
            // Mise à jour
            entity = existingOpt.get();
            entity.setUpcomingEventsDays(upcomingEventsDays);
            entity.setRequiredActionsDays(requiredActionsDays);
            entity.setEnabled(isEnabled);
            entity.setDeletedAt(null);
        } else {
            // Création
            entity = new HrAlertSettings();
            entity.setCompanyId(companyId);
            entity.setUserId(userId);
            entity.setUpcomingEventsDays(upcomingEventsDays);
            entity.setRequiredActionsDays(requiredActionsDays);
            entity.setEnabled(isEnabled);
        }

        return toDto(repository.save(entity));
    }

    // =========================
    // 🔄 Update spécifique
    // =========================
    public HrAlertSettingsResponsDTO update(
            Long id,
            Integer upcomingEventsDays,
            Integer requiredActionsDays,
            Boolean isEnabled
    ) {
        HrAlertSettings entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HrAlertSettings introuvable"));

        if (upcomingEventsDays != null) entity.setUpcomingEventsDays(upcomingEventsDays);
        if (requiredActionsDays != null) entity.setRequiredActionsDays(requiredActionsDays);
        if (isEnabled != null) entity.setEnabled(isEnabled);

        return toDto(repository.save(entity));
    }

    public HrAlertSettingsResponsDTO findByUserId(Long userId) {
        HrAlertSettings entity = repository
                .findFirstByUserIdAndDeletedAtIsNull(userId)
                .orElseGet(this::defaultSettings); // retourne les paramètres par défaut si aucun trouvé
        return toDto(entity);
    }
    // =========================
    // 🧹 Soft delete
    // =========================
    public void softDelete(Long id) {
        HrAlertSettings settings = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HrAlertSettings introuvable"));

        settings.softDelete();
        repository.save(settings);
    }

    // =========================
    // 🔧 Méthodes utilitaires
    // =========================
    private HrAlertSettings defaultSettings() {
        HrAlertSettings s = new HrAlertSettings();
        s.setUpcomingEventsDays(15);
        s.setRequiredActionsDays(7);
        s.setEnabled(true);
        return s;
    }

    private HrAlertSettingsResponsDTO toDto(HrAlertSettings entity) {
        return new HrAlertSettingsResponsDTO(
                entity.getId(),
                entity.getCompanyId(),
                entity.getUserId(),
                entity.getUpcomingEventsDays(),
                entity.getRequiredActionsDays(),
                entity.getEnabled()
        );
    }

}
