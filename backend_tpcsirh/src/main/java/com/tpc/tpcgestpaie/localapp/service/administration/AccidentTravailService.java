package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.AccidentTravailDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.AccidentTravail;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AccidentTravailRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccidentTravailService {

    private final AccidentTravailRepository accidentTravailRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public AccidentTravailService(AccidentTravailRepository accidentTravailRepository,
                                  EmployeRepository employeRepository,
                                  ContratEmployeRepository contratEmployeRepository,
                                  CompanyRepository companyRepository,
                                  UserRepository userRepository) {
        this.accidentTravailRepository = accidentTravailRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<AccidentTravailDTO> getAll(Pageable pageable) {
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findAllWithRelations(pageable);
        return accidentsPage.map(this::toDTO);
    }

    public Page<AccidentTravailDTO> findByCompanyId(Long companyId, Pageable pageable) {
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByCompanyId(companyId, pageable);
        return accidentsPage.map(this::toDTO);
    }

    public Page<AccidentTravailDTO> findByEmployeId(Long employeId, Pageable pageable) {
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByEmployeId(employeId, pageable);
        return accidentsPage.map(this::toDTO);
    }

    public Page<AccidentTravailDTO> findByDateAccidentBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByDateAccidentBetween(startDate, endDate, pageable);
        return accidentsPage.map(this::toDTO);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    public List<AccidentTravailDTO> getAll() {
        Pageable pageable = Pageable.unpaged();
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findAllWithRelations(pageable);
        return accidentsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<AccidentTravailDTO> getById(Long id) {
        return accidentTravailRepository.findById(id)
                .map(this::toDTO);
    }

    public AccidentTravailDTO save(AccidentTravailDTO dto) {
        AccidentTravail entity = toEntity(dto);
        AccidentTravail saved = accidentTravailRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public AccidentTravailDTO update(AccidentTravailDTO dto) {
        AccidentTravail existing = accidentTravailRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Accident avec ID introuvable"));

        existing.setDateAccident(dto.getDateAccident());
        existing.setDateDeclaration(dto.getDateDeclaration());
        existing.setEffetAccident(dto.getEffetAccident());
        existing.setAction(dto.getAction());
        existing.setDepense(dto.getDepense());
        existing.setUpdated_at(LocalDateTime.now());

        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new EntityNotFoundException("Employé introuvable"));
            existing.setEmploye(employe);
        }

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("Entreprise introuvable"));
            existing.setCompany(company);
        }

        AccidentTravail saved = accidentTravailRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        accidentTravailRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return accidentTravailRepository.existsById(id);
    }

    public List<AccidentTravailDTO> findByCompanyId(Long companyId) {
        Pageable pageable = Pageable.unpaged();
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByCompanyId(companyId, pageable);
        return accidentsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AccidentTravailDTO> findByEmployeId(Long employeId) {
        Pageable pageable = Pageable.unpaged();
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByEmployeId(employeId, pageable);
        return accidentsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AccidentTravailDTO> findByDateAccidentBetween(LocalDate startDate, LocalDate endDate) {
        Pageable pageable = Pageable.unpaged();
        Page<AccidentTravail> accidentsPage = accidentTravailRepository.findByDateAccidentBetween(startDate, endDate, pageable);
        return accidentsPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void softDelete(Long id) {
        AccidentTravail accident = accidentTravailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident introuvable"));

        accident.softDelete();
        accidentTravailRepository.save(accident);
    }

    // === MAPPING ===

    private AccidentTravailDTO toDTO(AccidentTravail entity) {
        AccidentTravailDTO dto = new AccidentTravailDTO();
        dto.setId(entity.getId());
        dto.setDateAccident(entity.getDateAccident());
        dto.setDateDeclaration(entity.getDateDeclaration());
        dto.setAction(entity.getAction());
        dto.setEffetAccident(entity.getEffetAccident());
        dto.setDepense(entity.getDepense());

        if (entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));
        }

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
        }

        if (entity.getContratEmploye() != null) {
            dto.setContratEmployeId(entity.getContratEmploye().getId());
        }

        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        return dto;
    }

    private AccidentTravail toEntity(AccidentTravailDTO dto) {
        AccidentTravail entity = new AccidentTravail();
        entity.setId(dto.getId());
        entity.setDateAccident(dto.getDateAccident());
        entity.setDateDeclaration(dto.getDateDeclaration());
        entity.setAction(dto.getAction());
        entity.setDepense(dto.getDepense());
        entity.setEffetAccident(dto.getEffetAccident());

        if (dto.getEmployeId() != null) {
            entity.setEmploye(employeRepository.findById(dto.getEmployeId()).orElse(null));
        }

        if (dto.getContratEmployeId() != null) {
            entity.setContratEmploye(contratEmployeRepository.findById(dto.getContratEmployeId()).orElse(null));
        }

        if (dto.getCompanyId() != null) {
            entity.setCompany(companyRepository.findById(dto.getCompanyId()).orElse(null));
        }

        if (dto.getAddedById() != null) {
            entity.setAdded_by(userRepository.findById(dto.getAddedById()).orElse(null));
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

    private CompanyDTO convertCompanyToDTO(Company company) {
        if (company == null) return null;
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        return dto;
    }
}