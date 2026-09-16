package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.FormationDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Formation;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.FormationRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FormationService {

    private final FormationRepository formationRepository;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;
    private final UserRepository userRepository;

    public FormationService(FormationRepository formationRepository, CompanyRepository companyRepository,
                            EmployeRepository employeRepository, UserRepository userRepository) {
        this.formationRepository = formationRepository;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
        this.userRepository = userRepository;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<FormationDTO> getAll(Pageable pageable) {
        Page<Formation> formationsPage = formationRepository.findAllWithAssociations(pageable);
        return formationsPage.map(FormationDTO::fromEntity);
    }

    public Page<FormationDTO> getAllByCompany(Long companyId, Pageable pageable) {
        Page<Formation> formationsPage = formationRepository.findByCompanyId(companyId, pageable);
        return formationsPage.map(FormationDTO::fromEntity);
    }

    public Page<FormationDTO> getAllByEmploye(Long employeId, Pageable pageable) {
        Page<Formation> formationsPage = formationRepository.findByEmployeId(employeId, pageable);
        return formationsPage.map(FormationDTO::fromEntity);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    public List<FormationDTO> getAll() {
        return formationRepository.findAll().stream()
                .map(FormationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<FormationDTO> getById(Long id) {
        return formationRepository.findById(id)
                .map(FormationDTO::fromEntity);
    }

    @Transactional
    public FormationDTO save(FormationDTO dto) {
        Formation formation;

        if (dto.getId() != null) {
            // UPDATE
            formation = formationRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Formation not found"));
        } else {
            // CREATE
            formation = new Formation();

            if (dto.getAddedById() != null) {
                User addedBy = userRepository.findById(dto.getAddedById())
                        .orElseThrow(() -> new EntityNotFoundException("User (addedBy) not found"));
                formation.setAddedBy(addedBy);
            } else {
                throw new IllegalArgumentException("addedById est requis pour la création");
            }

            formation.setCreatedAt(LocalDateTime.now());
        }

        // Récupération de la company via companyId
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found"));
            formation.setCompany(company);
        } else {
            throw new IllegalArgumentException("Company est requis");
        }

        formation.setTheme(dto.getTheme());
        formation.setDescription(dto.getDescription());
        formation.setLieu(dto.getLieu());
        formation.setDateDebut(dto.getDateDebut());
        formation.setDateFin(dto.getDateFin());
        formation.setDuree(dto.getDuree());

        // Gestion du updatedBy
        if (dto.getUpdatedById() != null) {
            User updatedBy = userRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User (updatedBy) not found"));
            formation.setUpdated_by(updatedBy);
        }

        formation.setUpdatedAt(LocalDateTime.now());

        // Gestion des employés liés
        if (dto.getEmployeIds() != null && !dto.getEmployeIds().isEmpty()) {
            List<Employe> employes = employeRepository.findAllById(dto.getEmployeIds());
            if (employes.size() != dto.getEmployeIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs employés spécifiés n'existent pas");
            }
            formation.setEmployes(employes);
        } else {
            formation.getEmployes().clear();
        }

        Formation saved = formationRepository.save(formation);
        return FormationDTO.fromEntity(saved);
    }

    @Transactional
    public FormationDTO update(Long id, FormationDTO updatedDto, User currentUser) {
        Formation existing = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation avec l'ID " + id + " introuvable"));

        // Mise à jour des champs simples
        existing.setTheme(updatedDto.getTheme());
        existing.setDescription(updatedDto.getDescription());
        existing.setLieu(updatedDto.getLieu());
        existing.setDateDebut(updatedDto.getDateDebut());
        existing.setDateFin(updatedDto.getDateFin());
        existing.setDuree(updatedDto.getDuree());

        // Mise à jour des employés
        if (updatedDto.getEmployeIds() != null && !updatedDto.getEmployeIds().isEmpty()) {
            List<Employe> employes = employeRepository.findAllById(updatedDto.getEmployeIds());
            if (employes.size() != updatedDto.getEmployeIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs employés spécifiés n'existent pas");
            }
            existing.setEmployes(employes);
        } else {
            throw new IllegalArgumentException("Au moins un employé doit être associé");
        }

        // Mise à jour du suivi utilisateur
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdated_by(currentUser);

        Formation saved = formationRepository.save(existing);
        return FormationDTO.fromEntity(saved);
    }

    @Transactional
    public void softDelete(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation introuvable avec l'ID " + id));

        formation.softDelete();
        formationRepository.save(formation);
    }

    public void delete(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation not found"));
        formation.setUpdatedAt(LocalDateTime.now());
        formationRepository.delete(formation);
    }

    public boolean exists(Long id) {
        return formationRepository.existsById(id);
    }
}