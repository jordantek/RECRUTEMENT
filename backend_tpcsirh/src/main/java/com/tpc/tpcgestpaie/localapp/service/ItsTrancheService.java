package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.ItsTrancheDTO;
import com.tpc.tpcgestpaie.localapp.model.ItsTranche;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ItsTrancheRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItsTrancheService {

    private final ItsTrancheRepository repository;
    private final UserRepository userRepository;

    public ItsTrancheService(ItsTrancheRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // Convert entity -> DTO
    private ItsTrancheDTO convertToDTO(ItsTranche entity) {
        ItsTrancheDTO dto = new ItsTrancheDTO();
        dto.setId(entity.getId());
        dto.setLimiteTranche1(entity.getLimiteTranche1());
        dto.setLimiteTranche2(entity.getLimiteTranche2());
        dto.setLimiteTranche3(entity.getLimiteTranche3());
        dto.setLimiteTranche4(entity.getLimiteTranche4());
        dto.setRateTranche1(entity.getRateTranche1());
        dto.setRateTranche2(entity.getRateTranche2());
        dto.setRateTranche3(entity.getRateTranche3());
        dto.setRateTranche4(entity.getRateTranche4());
        dto.setRateAbattement(entity.getRateAbattement());
        if (entity.getAdded_by() != null) {
            dto.setAddedById(entity.getAdded_by().getId());
        }
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        return dto;
    }

    // 🔹 Get unique
    public ItsTrancheDTO getUnique() {
        ItsTranche entity = repository.findByUniqueKey("PARAM_UNIQUE")
                .orElseThrow(() -> new RuntimeException("Aucune tranche trouvée"));
        return convertToDTO(entity);
    }

    // 🔹 Save or Update
    public ItsTrancheDTO saveOrUpdate(ItsTrancheDTO dto) {
        Optional<ItsTranche> optional = repository.findByUniqueKey("PARAM_UNIQUE");
        ItsTranche entity = optional.orElseGet(ItsTranche::new);

        entity.setLimiteTranche1(dto.getLimiteTranche1());
        entity.setLimiteTranche2(dto.getLimiteTranche2());
        entity.setLimiteTranche3(dto.getLimiteTranche3());
        entity.setLimiteTranche4(dto.getLimiteTranche4());
        entity.setRateTranche1(dto.getRateTranche1());
        entity.setRateTranche2(dto.getRateTranche2());
        entity.setRateTranche3(dto.getRateTranche3());
        entity.setRateTranche4(dto.getRateTranche4());
        entity.setRateAbattement(dto.getRateAbattement());

        if (dto.getAddedById() != null) {
            User user = userRepository.findById(dto.getAddedById())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            entity.setAdded_by(user);
        }

        entity.setUniqueKey("PARAM_UNIQUE");

        return convertToDTO(repository.save(entity));
    }

    // 🔹 Update explicite
    public ItsTrancheDTO update(ItsTrancheDTO dto) {
        ItsTranche entity = repository.findByUniqueKey("PARAM_UNIQUE")
                .orElseThrow(() -> new RuntimeException("Tranche introuvable, créez-la d'abord"));

        entity.setLimiteTranche1(dto.getLimiteTranche1());
        entity.setLimiteTranche2(dto.getLimiteTranche2());
        entity.setLimiteTranche3(dto.getLimiteTranche3());
        entity.setLimiteTranche4(dto.getLimiteTranche4());
        entity.setRateTranche1(dto.getRateTranche1());
        entity.setRateTranche2(dto.getRateTranche2());
        entity.setRateTranche3(dto.getRateTranche3());
        entity.setRateTranche4(dto.getRateTranche4());
        entity.setRateAbattement(dto.getRateAbattement());

        if (dto.getAddedById() != null) {
            User user = userRepository.findById(dto.getAddedById())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            entity.setAdded_by(user);
        }

        return convertToDTO(repository.save(entity));
    }
}
