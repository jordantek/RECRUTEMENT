package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.EnfantEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EnfantEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EnfantEmployeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnfantEmployeService {
    private final EnfantEmployeRepository repository;
    private final UserService userService;
    private final EmployeRepository employeRepository;

    public EnfantEmployeService(EnfantEmployeRepository repository, UserService userService, EmployeRepository employeRepository) {
        this.repository = repository;
        this.userService = userService;
        this.employeRepository = employeRepository;
    }

    public List<EnfantEmploye> findAll() {
        return repository.findAll();
    }


    public EnfantEmploye findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
    }

    public List<EnfantEmploye> findByEmployeId(Long employeId) {
        return repository.findByEmployeIdAndDeletedAtIsNull(employeId);
    }

    public int countByEmployeId(Long employeId) {
        return repository.countByEmployeIdAndDeletedAtIsNull(employeId);
    }

    public EnfantEmploye create(EnfantEmploye enfant, Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID : " + employeId));
        enfant.setEmploye(employe);

        enfant.setCreatedAt(LocalDateTime.now());
        enfant.setUpdatedAt(LocalDateTime.now());
        enfant.setCreatedBy(userService.getCurrentUser());
        enfant.setUpdatedBy(userService.getCurrentUser());

        return repository.save(enfant);
    }

    public EnfantEmploye update(Long id, EnfantEmploye updated) {
        EnfantEmploye enfant = findById(id);
        enfant.setNom(updated.getNom());
        enfant.setPrenom(updated.getPrenom());
        enfant.setDateNaissance(updated.getDateNaissance());
        enfant.setLieuNaissance(updated.getLieuNaissance());
        enfant.setSexe(updated.getSexe());
        enfant.setUpdatedAt(LocalDateTime.now());
        enfant.setUpdatedBy(userService.getCurrentUser());
        return repository.save(enfant);
    }

    public void softDelete(Long id) {
        Optional<EnfantEmploye> optional = repository.findById(id);
        if (optional.isPresent()) {
            EnfantEmploye enfant = optional.get();
            enfant.setDeletedAt(LocalDateTime.now());
            repository.save(enfant);
        } else {
            throw new EntityNotFoundException("EnfantEmploye non trouvé avec l'id " + id);
        }

    }
    public EnfantEmployeDTO getEnfant(EnfantEmploye entity) {
        EnfantEmployeDTO dto = new EnfantEmployeDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setSexe(entity.getSexe());
        dto.setDateNaissance(entity.getDateNaissance());
        dto.setLieuNaissance(entity.getLieuNaissance());
        dto.setEstDecede(entity.getEstDecede());
       return dto;
    }
    /*public void delete(Long id) {
        repository.deleteById(id);
    }*/

    public EnfantEmploye mapToEnfant(
            EnfantEmployeRequestDTO dto,
            Employe employe,
            User user
    ) {
        EnfantEmploye enfant = new EnfantEmploye();

        enfant.setNom(dto.nom());
        enfant.setPrenom(dto.prenom());
        enfant.setDateNaissance(dto.dateNaissance());

        enfant.setEmploye(employe);
        enfant.setCreatedAt(LocalDateTime.now());
        enfant.setCreatedBy(user);

        return enfant;
    }
}
