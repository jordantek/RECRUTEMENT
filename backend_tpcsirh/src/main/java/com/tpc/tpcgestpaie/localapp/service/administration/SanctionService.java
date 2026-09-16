package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.SanctionDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Sanction;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.SanctionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SanctionService {

    private final SanctionRepository sanctionRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public SanctionService(SanctionRepository sanctionRepository, EmployeRepository employeRepository,
                           ContratEmployeRepository contratEmployeRepository, CompanyRepository companyRepository,
                           UserRepository userRepository) {
        this.sanctionRepository = sanctionRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<SanctionDTO> findAll(Pageable pageable) {
        Page<Sanction> sanctionsPage = sanctionRepository.findAllWithAssociations(pageable);
        return sanctionsPage.map(this::toDTO);
    }

    public Page<SanctionDTO> findByCompanyId(Long companyId, Pageable pageable) {
        Page<Sanction> sanctionsPage = sanctionRepository.findByCompanyId(companyId, pageable);
        return sanctionsPage.map(this::toDTO);
    }

    public Page<SanctionDTO> findByEmployeId(Long employeId, Pageable pageable) {
        Page<Sanction> sanctionsPage = sanctionRepository.findByEmployeId(employeId, pageable);
        return sanctionsPage.map(this::toDTO);
    }

    public Optional<SanctionDTO> findById(Long id) {
        return sanctionRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public SanctionDTO save(SanctionDTO dto) {
        Sanction entity = toEntity(dto);
        return toDTO(sanctionRepository.save(entity));
    }

    @Transactional
    public SanctionDTO update(SanctionDTO dto) {
        Sanction existing = sanctionRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sanction avec l'ID " + dto.getId() + " introuvable"));

        // Mise à jour des champs simples
        existing.setDatePlainte(dto.getDatePlainte());
        existing.setContenuePlainte(dto.getContenuePlainte());
        existing.setDateDemandeExplication(dto.getDateDemandeExplication());
        existing.setDateReponse(dto.getDateReponse());
        existing.setSanctionDonnee(dto.getSanctionDonnee());
        existing.setObservation(dto.getObservation());
        existing.setUpdated_at(LocalDateTime.now());

        // Relations
        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new EntityNotFoundException("Employé introuvable"));
            existing.setEmploye(employe);
        }

        if (dto.getContratEmployeId() != null) {
            ContratEmploye contrat = contratEmployeRepository.findById(dto.getContratEmployeId())
                    .orElseThrow(() -> new EntityNotFoundException("Contrat employé introuvable"));
            existing.setContratEmploye(contrat);
        }

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Entreprise introuvable"));
            existing.setCompany(company);
        }

        Sanction saved = sanctionRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        sanctionRepository.deleteById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Sanction sanction = sanctionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sanction introuvable"));
        sanction.softDelete();
        sanctionRepository.save(sanction);
    }

    // === MAPPING ===

    private SanctionDTO toDTO(Sanction entity) {
        SanctionDTO dto = new SanctionDTO();
        dto.setId(entity.getId());

        if (entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));
        }

        if (entity.getContratEmploye() != null) {
            dto.setContratEmployeId(entity.getContratEmploye().getId());
            dto.setContratEmploye(convertContratToDTO(entity.getContratEmploye()));
        }

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
            dto.setCompany(convertCompanyToDTO(entity.getCompany()));
        }

        dto.setDatePlainte(entity.getDatePlainte());
        dto.setContenuePlainte(entity.getContenuePlainte());
        dto.setDateDemandeExplication(entity.getDateDemandeExplication());
        dto.setDateReponse(entity.getDateReponse());
        dto.setSanctionDonnee(entity.getSanctionDonnee());
        dto.setObservation(entity.getObservation());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedById(entity.getUpdated_by() != null ? entity.getUpdated_by().getId() : null);
        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setDeletedAt(entity.getDeleted_at());
        return dto;
    }

    private Sanction toEntity(SanctionDTO dto) {
        Sanction entity = new Sanction();
        entity.setId(dto.getId());
        entity.setDatePlainte(dto.getDatePlainte());
        entity.setContenuePlainte(dto.getContenuePlainte());
        entity.setDateDemandeExplication(dto.getDateDemandeExplication());
        entity.setDateReponse(dto.getDateReponse());
        entity.setSanctionDonnee(dto.getSanctionDonnee());
        entity.setObservation(dto.getObservation());

        // Liaison Employe
        if (dto.getEmployeId() != null) {
            entity.setEmploye(employeRepository.findById(dto.getEmployeId()).orElse(null));
        }

        // Liaison ContratEmploye
        if (dto.getContratEmployeId() != null) {
            entity.setContratEmploye(contratEmployeRepository.findById(dto.getContratEmployeId()).orElse(null));
        }

        // Liaison Company
        if (dto.getCompanyId() != null) {
            entity.setCompany(companyRepository.findById(dto.getCompanyId()).orElse(null));
        }

        // Liaison AddedBy
        if (dto.getAddedById() != null) {
            entity.setAdded_by(userRepository.findById(dto.getAddedById()).orElse(null));
        }

        // Liaison UpdatedBy
        if (dto.getUpdatedById() != null) {
            entity.setUpdated_by(userRepository.findById(dto.getUpdatedById()).orElse(null));
        }

        return entity;
    }

    private EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        dto.setEmail(employe.getEmail());
        return dto;
    }

    private ContratEmploye convertContratToDTO(ContratEmploye contrat) {
        if (contrat == null) return null;

        ContratEmploye dto = new ContratEmploye();
        dto.setId(contrat.getId());
        dto.setType_contrat(contrat.getType_contrat());
        dto.setDate_debut(contrat.getDate_debut());
        dto.setDate_fin(contrat.getDate_fin());
        return dto;
    }

    private CompanyDTO convertCompanyToDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        return dto;
    }
}