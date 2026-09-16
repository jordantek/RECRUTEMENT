package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.SettingsApp.SettingsAppResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.SettingsApp;
import com.tpc.tpcgestpaie.localapp.repository.SettingsAppRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsAppService {

    private final SettingsAppRepository settingRepository;

    public SettingsApp saveThemeColor(Long companyId, String color) {

        Optional<SettingsApp> optionalSetting = settingRepository.findByCompanyId(companyId);

        SettingsApp setting;

        if (optionalSetting.isPresent()) {
            // Mise à jour
            setting = optionalSetting.get();
            setting.setThemeColor(color);
        } else {
            // Création
            setting = new SettingsApp();
            setting.setCompanyId(companyId);
            setting.setThemeColor(color);
        }

        return settingRepository.save(setting);
    }


    // Récupère la config d'une entreprise, ou crée un objet par défaut si aucune
    public SettingsApp getSettingsByCompany(Long companyId) {
        return settingRepository
                .findByCompanyId(companyId)
                .orElseGet(() -> {
                    SettingsApp defaultSetting = new SettingsApp();
                    defaultSetting.setCompanyId(companyId);
                    defaultSetting.setThemeColor("#2E7DAF"); // couleur par défaut
                    return defaultSetting;
                });
    }

    // Récupère la première config disponible, ou une valeur par défaut
    public SettingsApp getFirstSettings() {
        return settingRepository
                .findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    SettingsApp defaultSetting = new SettingsApp();
                    defaultSetting.setCompanyId(null); // pas de companyId
                    defaultSetting.setThemeColor("#2E7DAF");
                    return defaultSetting;
                });
    }

    public SettingsAppResponseDTO mapToResponse(SettingsApp setting) {
        return new SettingsAppResponseDTO(
                setting.getId(),
                setting.getCompanyId(),
                setting.getThemeColor()
        );
    }

}