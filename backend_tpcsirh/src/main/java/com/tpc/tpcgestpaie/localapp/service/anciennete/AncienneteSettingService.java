package com.tpc.tpcgestpaie.localapp.service.anciennete;

import com.tpc.tpcgestpaie.localapp.dto.anciennete.AncienneteSettingDTO;
import com.tpc.tpcgestpaie.localapp.model.anciennete.AncienneteSetting;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.anciennete.AncienneteSettingRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AncienneteSettingService {

    @Autowired
    private AncienneteSettingRepository ancienneteSettingRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    public List<AncienneteSettingDTO> getAll() {
        return ancienneteSettingRepository.findAllWithDetails()
                .stream()
                .map(AncienneteSettingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AncienneteSettingDTO> getById(Long id) {
        return ancienneteSettingRepository.findById(id)
                .map(AncienneteSettingDTO::fromEntity);
    }

    public Optional<AncienneteSettingDTO> getByCompanyId(Long companyId) {
        return ancienneteSettingRepository.findByCompanyId(companyId)
                .map(AncienneteSettingDTO::fromEntity);
    }

    public Optional<AncienneteSettingDTO> getActiveAncienneteByCompanyId(Long companyId) {
        return ancienneteSettingRepository.findActiveAncienneteByCompanyId(companyId)
                .map(AncienneteSettingDTO::fromEntity);
    }

    public AncienneteSettingDTO save(AncienneteSettingDTO dto) {
        User currentUser = userService.getCurrentUser();
        Company company = companyService.getById(dto.getCompanyId());

        if (company == null) {
            throw new IllegalArgumentException("Company not found with ID: " + dto.getCompanyId());
        }

        // Vérifier si un setting existe déjà pour cette entreprise
        Optional<AncienneteSetting> existingSetting =
                ancienneteSettingRepository.findByCompanyId(company.getId());

        AncienneteSetting entity;

        if (existingSetting.isPresent()) {
            // Mise à jour du setting existant
            entity = existingSetting.get();
            entity.setNombreMoisEcart(dto.getNombreMoisEcart());
            entity.setPayeAnciennete(dto.getPayeAnciennete() != null ? dto.getPayeAnciennete() : false);
            entity.setAdded_by(currentUser);
        } else {
            // Création d'un nouveau setting
            entity = dto.toEntity(company, currentUser);
        }

        AncienneteSetting saved = ancienneteSettingRepository.save(entity);
        return AncienneteSettingDTO.fromEntity(saved);
    }

    public AncienneteSettingDTO createOrUpdate(Long companyId, Integer nombreMoisEcart, Boolean payeAnciennete) {
        User currentUser = userService.getCurrentUser();
        Company company = companyService.getById(companyId);

        if (company == null) {
            throw new IllegalArgumentException("Company not found with ID: " + companyId);
        }

        Optional<AncienneteSetting> existingSetting =
                ancienneteSettingRepository.findByCompanyId(companyId);

        AncienneteSetting entity;

        if (existingSetting.isPresent()) {
            entity = existingSetting.get();
            entity.setNombreMoisEcart(nombreMoisEcart);
            entity.setPayeAnciennete(payeAnciennete != null ? payeAnciennete : false);
            entity.setAdded_by(currentUser);
        } else {
            entity = new AncienneteSetting();
            entity.setCompany(company);
            entity.setNombreMoisEcart(nombreMoisEcart);
            entity.setPayeAnciennete(payeAnciennete != null ? payeAnciennete : false);
            entity.setAdded_by(currentUser);
        }

        AncienneteSetting saved = ancienneteSettingRepository.save(entity);
        return AncienneteSettingDTO.fromEntity(saved);
    }

    public void delete(Long id) {
        Optional<AncienneteSetting> settingOpt = ancienneteSettingRepository.findById(id);
        if (settingOpt.isPresent()) {
            AncienneteSetting setting = settingOpt.get();
            setting.setDeleted_at(LocalDateTime.now());
            ancienneteSettingRepository.save(setting);
        }
    }

    public void hardDelete(Long id) {
        ancienneteSettingRepository.deleteById(id);
    }

    public boolean existsByCompanyId(Long companyId) {
        return ancienneteSettingRepository.existsByCompanyId(companyId);
    }

    public boolean isAncienneteEnabledForCompany(Long companyId) {
        Optional<AncienneteSetting> setting = ancienneteSettingRepository
                .findActiveAncienneteByCompanyId(companyId);
        return setting.isPresent() &&
                Boolean.TRUE.equals(setting.get().getPayeAnciennete()) &&
                setting.get().getNombreMoisEcart() != null &&
                setting.get().getNombreMoisEcart() > 0;
    }

    public Integer getEcartMoisForCompany(Long companyId) {
        return ancienneteSettingRepository
                .findActiveAncienneteByCompanyId(companyId)
                .map(AncienneteSetting::getNombreMoisEcart)
                .orElse(null);
    }
}