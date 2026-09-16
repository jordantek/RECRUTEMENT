package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.ParametrePaieDTO;
import com.tpc.tpcgestpaie.localapp.model.ParametrePaie;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ParametrePaieRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParametrePaieService {

    private final ParametrePaieRepository parametrePaieRepository;
    private final UserRepository userRepository;

    public ParametrePaieService(ParametrePaieRepository parametrePaieRepository, UserRepository userRepository) {
        this.parametrePaieRepository = parametrePaieRepository;
        this.userRepository = userRepository;
    }

    // Convert entity -> DTO
    private ParametrePaieDTO convertToDTO(ParametrePaie entity) {
        ParametrePaieDTO dto = new ParametrePaieDTO();
        dto.setId(entity.getId());
        dto.setPrestationsfamiliales(entity.getPrestationsfamiliales());
        dto.setPensionsEntreprise(entity.getPensionsEntreprise());
        dto.setPensionsEmploye(entity.getPensionsEmploye());
        dto.setNbrAnneVpsEntreprise(entity.getNbrAnneVpsEntreprise());
        dto.setNbrAnneVpsEmploye(entity.getNbrAnneVpsEmploye());
        if (entity.getAdded_by() != null) {
            dto.setAddedById(entity.getAdded_by().getId());
        }
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        return dto;
    }

    // Convert DTO -> entity
    private ParametrePaie convertToEntity(ParametrePaieDTO dto) {
        ParametrePaie entity = new ParametrePaie();
        entity.setId(dto.getId());
        entity.setPrestationsfamiliales(dto.getPrestationsfamiliales());
        entity.setPensionsEntreprise(dto.getPensionsEntreprise());
        entity.setPensionsEmploye(dto.getPensionsEmploye());
        entity.setNbrAnneVpsEntreprise(dto.getNbrAnneVpsEntreprise());
        entity.setNbrAnneVpsEmploye(dto.getNbrAnneVpsEmploye());
        if (dto.getAddedById() != null) {
            User user = userRepository.findById(dto.getAddedById())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            entity.setAdded_by(user);
        }
        return entity;
    }

    public List<ParametrePaieDTO> getAll() {
        return parametrePaieRepository.findAll()
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ParametrePaieDTO getById(Long id) {
        return parametrePaieRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Paramètre non trouvé"));
    }

    public ParametrePaieDTO create(ParametrePaieDTO dto) {
        ParametrePaie entity = convertToEntity(dto);
        return convertToDTO(parametrePaieRepository.save(entity));
    }

    @Transactional
    public ParametrePaieDTO getUnique() {
        return parametrePaieRepository.findByUniqueKey("PARAM_UNIQUE")
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Aucun paramètre trouvé"));
    }

    @Transactional
    public ParametrePaieDTO update(ParametrePaieDTO dto) {
        ParametrePaie entity = parametrePaieRepository.findByUniqueKey("PARAM_UNIQUE")
                .orElseThrow(() -> new RuntimeException("Paramètre introuvable, créez-le d’abord."));

        entity.setPrestationsfamiliales(dto.getPrestationsfamiliales());
        entity.setPensionsEntreprise(dto.getPensionsEntreprise());
        entity.setPensionsEmploye(dto.getPensionsEmploye());
        entity.setNbrAnneVpsEntreprise(dto.getNbrAnneVpsEntreprise());
        entity.setNbrAnneVpsEmploye(dto.getNbrAnneVpsEmploye());
        return convertToDTO(parametrePaieRepository.save(entity));
    }


    public ParametrePaieDTO createUnique(ParametrePaieDTO dto) {
        // Vérifier s'il existe déjà un paramètre
        long count = parametrePaieRepository.count();
        if (count > 0) {
            throw new RuntimeException("Un paramètre existe déjà. Utilisez la mise à jour.");
        }

        ParametrePaie entity = convertToEntity(dto);
        return convertToDTO(parametrePaieRepository.save(entity));
    }

    @Transactional
    public ParametrePaieDTO saveOrUpdate(ParametrePaieDTO dto) {
        // Chercher l’unique paramètre existant
        ParametrePaie entity = parametrePaieRepository.findByUniqueKey("PARAM_UNIQUE")
                .orElse(new ParametrePaie()); // si non trouvé -> nouvel objet

        // Mise à jour des valeurs
        entity.setPrestationsfamiliales(dto.getPrestationsfamiliales());
        entity.setPensionsEntreprise(dto.getPensionsEntreprise());
        entity.setPensionsEmploye(dto.getPensionsEmploye());
        entity.setNbrAnneVpsEntreprise(dto.getNbrAnneVpsEntreprise());
        entity.setNbrAnneVpsEmploye(dto.getNbrAnneVpsEmploye());

        // Associer un utilisateur si fourni
//        if (dto.getAddedById() != null) {
//            User user = userRepository.findById(dto.getAddedById())
//                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
//            entity.setAdded_by(user);
//        }

        // Forcer la clé unique
        entity.setUniqueKey("PARAM_UNIQUE");

        return convertToDTO(parametrePaieRepository.save(entity));
    }


    public ParametrePaieDTO update(Long id, ParametrePaieDTO dto) {
        ParametrePaie entity = parametrePaieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètre non trouvé"));
        entity.setPrestationsfamiliales(dto.getPrestationsfamiliales());
        entity.setPensionsEntreprise(dto.getPensionsEntreprise());
        entity.setPensionsEmploye(dto.getPensionsEmploye());
        entity.setNbrAnneVpsEntreprise(dto.getNbrAnneVpsEntreprise());
        entity.setNbrAnneVpsEmploye(dto.getNbrAnneVpsEmploye());
        return convertToDTO(parametrePaieRepository.save(entity));
    }

    public void delete(Long id) {
        parametrePaieRepository.deleteById(id);
    }
}
