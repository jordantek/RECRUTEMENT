package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.EvenementSocialDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EvenementSocial;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.EvenementSocialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvenementSocialService {

    private final EvenementSocialRepository evenementSocialRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public EvenementSocialService(EvenementSocialRepository evenementSocialRepository,
                                  EmployeRepository employeRepository,
                                  ContratEmployeRepository contratEmployeRepository,
                                  CompanyRepository companyRepository,
                                  UserRepository userRepository) {
        this.evenementSocialRepository = evenementSocialRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<EvenementSocialDTO> findAll(Pageable pageable) {
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findAllWithAssociations(pageable);
        return evenementsPage.map(this::toDTO);
    }

    public Page<EvenementSocialDTO> findByCompanyId(Long companyId, Pageable pageable) {
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findByCompanyId(companyId, pageable);
        return evenementsPage.map(this::toDTO);
    }

    public Page<EvenementSocialDTO> findByEmployeId(Long employeId, Pageable pageable) {
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findByEmployeId(employeId, pageable);
        return evenementsPage.map(this::toDTO);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    // SUPPRIMER ou CORRIGER ces méthodes si elles ne sont plus utilisées
    // Si vous avez besoin de méthodes non paginées, utilisez findAll() sans Pageable
    // ou créez des méthodes spécifiques dans le repository

    public Optional<EvenementSocialDTO> findById(Long id) {
        return evenementSocialRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public EvenementSocialDTO save(EvenementSocialDTO dto) {
        EvenementSocial entity = toEntity(dto);
        return toDTO(evenementSocialRepository.save(entity));
    }

    @Transactional
    public EvenementSocialDTO update(EvenementSocialDTO dto) {
        EvenementSocial existing = evenementSocialRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Événement social avec l'ID " + dto.getId() + " introuvable"));

        // Mise à jour des champs simples
        existing.setDateEvenement(dto.getDateEvenement());
        existing.setDesignation(dto.getDesignation());
        existing.setActionMenee(dto.getActionMenee());
        existing.setMontant(dto.getMontant());
        existing.setObservation(dto.getObservation());
        existing.setUpdated_at(LocalDateTime.now());

        // Sauvegarde & conversion
        EvenementSocial saved = evenementSocialRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        evenementSocialRepository.deleteById(id);
    }

    // === MÉTHODES DE COMPATIBILITÉ - À SUPPRIMER SI NON UTILISÉES ===

    // Si vous avez vraiment besoin de ces méthodes non paginées,
    // vous devez les implémenter différemment ou utiliser la pagination avec une grande taille
    public List<EvenementSocialDTO> findAllNonPagine() {
        // Utiliser une pagination avec une grande taille pour récupérer tous les éléments
        Pageable pageable = Pageable.unpaged(); // ou PageRequest.of(0, Integer.MAX_VALUE)
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findAllWithAssociations(pageable);
        return evenementsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EvenementSocialDTO> findByCompanyIdNonPagine(Long companyId) {
        Pageable pageable = Pageable.unpaged();
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findByCompanyId(companyId, pageable);
        return evenementsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EvenementSocialDTO> findByEmployeIdNonPagine(Long employeId) {
        Pageable pageable = Pageable.unpaged();
        Page<EvenementSocial> evenementsPage = evenementSocialRepository.findByEmployeId(employeId, pageable);
        return evenementsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void softDelete(Long id) {
        EvenementSocial evenementSocial = evenementSocialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement social introuvable"));

        evenementSocial.softDelete();
        evenementSocialRepository.save(evenementSocial);
    }

    // === MAPPING ===

    private EvenementSocialDTO toDTO(EvenementSocial entity) {
        EvenementSocialDTO dto = new EvenementSocialDTO();
        dto.setId(entity.getId());

        if (entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));
        }

        if (entity.getContratEmploye() != null) {
            dto.setContratEmployeId(entity.getContratEmploye().getId());
        }

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
        }

        dto.setDateEvenement(entity.getDateEvenement());
        dto.setDesignation(entity.getDesignation());
        dto.setActionMenee(entity.getActionMenee());
        dto.setMontant(entity.getMontant());
        dto.setObservation(entity.getObservation());
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedById(entity.getUpdated_by() != null ? entity.getUpdated_by().getId() : null);
        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setDeletedAt(entity.getDeleted_at());

        return dto;
    }

    private EvenementSocial toEntity(EvenementSocialDTO dto) {
        EvenementSocial entity = new EvenementSocial();
        entity.setId(dto.getId());
        entity.setDateEvenement(dto.getDateEvenement());
        entity.setDesignation(dto.getDesignation());
        entity.setActionMenee(dto.getActionMenee());
        entity.setMontant(dto.getMontant());
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