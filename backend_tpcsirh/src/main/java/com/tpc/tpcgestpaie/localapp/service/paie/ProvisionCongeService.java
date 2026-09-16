//package com.tpc.tpcgestpaie.localapp.service.paie;
//
//import com.tpc.tpcgestpaie.localapp.dto.paie.ProvisionCongeDTO;
//import com.tpc.tpcgestpaie.localapp.model.*;
//import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
//import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
//import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
//import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
//import com.tpc.tpcgestpaie.localapp.service.UserService;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class ProvisionCongeService {
//
//    private final ProvisionCongeRepository provisionCongeRepository;
//    private final EmployeRepository employeRepository;
//    private final ContratEmployeRepository contratEmployeRepository;
//    private final CompanyRepository companyRepository;
//    private final CreditCongeRepository creditCongeRepository;
//    private final UserService userService;
//
//    public ProvisionCongeService(ProvisionCongeRepository provisionCongeRepository,
//                                 EmployeRepository employeRepository,
//                                 ContratEmployeRepository contratEmployeRepository,
//                                 CompanyRepository companyRepository,
//                                 CreditCongeRepository creditCongeRepository,
//                                 UserService userService) {
//        this.provisionCongeRepository = provisionCongeRepository;
//        this.employeRepository = employeRepository;
//        this.contratEmployeRepository = contratEmployeRepository;
//        this.companyRepository = companyRepository;
//        this.creditCongeRepository = creditCongeRepository;
//        this.userService = userService;
//    }
//
//    public List<ProvisionCongeDTO> findAll() {
//        return provisionCongeRepository.findAll()
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public Optional<ProvisionCongeDTO> findById(Long id) {
//        return provisionCongeRepository.findById(id).map(this::toDTO);
//    }
//
//    public List<ProvisionCongeDTO> findByMoisAndCompany(String moisProvisionConge, Long companyId) {
//        return provisionCongeRepository.findByMoisProvisionCongeAndCompanyId(moisProvisionConge, companyId)
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<ProvisionCongeDTO> findByMoisCompanyEmploye(String moisProvisionConge, Long companyId, Long employeId) {
//        return provisionCongeRepository.findByMoisProvisionCongeAndCompanyIdAndEmployeId(moisProvisionConge, companyId, employeId)
//                .stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public ProvisionCongeDTO save(ProvisionCongeDTO dto) {
//        Employe employe = employeRepository.findById(dto.getEmployeId())
//                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
//        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
//                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
//        Company company = companyRepository.findById(dto.getCompanyId())
//                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
//        CreditConge creditConge = creditCongeRepository.findById(dto.getCreditCongeId())
//                .orElseThrow(() -> new RuntimeException("Crédit congé introuvable"));
//        User user = userService.getCurrentUser();
//
//        ProvisionConge entity = toEntity(dto, employe, contratEmploye, company, creditConge, user);
//        ProvisionConge saved = provisionCongeRepository.save(entity);
//
//        return toDTO(saved);
//    }
//
//    @Transactional
//    public ProvisionCongeDTO update(Long id, ProvisionCongeDTO dto) {
//        ProvisionConge existing = provisionCongeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Provision congé introuvable"));
//
//        Employe employe = employeRepository.findById(dto.getEmployeId())
//                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
//        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
//                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
//        Company company = companyRepository.findById(dto.getCompanyId())
//                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
//        CreditConge creditConge = creditCongeRepository.findById(dto.getCreditCongeId())
//                .orElseThrow(() -> new RuntimeException("Crédit congé introuvable"));
//        User user = userService.getCurrentUser();
//
//        existing.setMoisProvisionConge(dto.getMoisProvisionConge());
//        existing.setNombreJourAccorde(dto.getNombreJourAccorde());
//        existing.setMontantProvisionConge(dto.getMontantProvisionConge());
//        existing.setEmploye(employe);
//        existing.setContratEmploye(contratEmploye);
//        existing.setCompany(company);
//        existing.setCreditConge(creditConge);
//        existing.setLastUpdateUser(user);
//        existing.setUpdatedAt(LocalDateTime.now());
//
//        ProvisionConge saved = provisionCongeRepository.save(existing);
//        return toDTO(saved);
//    }
//
//    public void delete(Long id) {
//        provisionCongeRepository.deleteById(id);
//    }
//
//    // === Mapping ===
//
//    public ProvisionCongeDTO toDTO(ProvisionConge entity) {
//        ProvisionCongeDTO dto = new ProvisionCongeDTO();
//        dto.setId(entity.getId());
//        dto.setMoisProvisionConge(entity.getMoisProvisionConge());
//        dto.setNombreJourAccorde(entity.getNombreJourAccorde());
//        dto.setMontantProvisionConge(entity.getMontantProvisionConge());
//        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
//        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
//        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
//        dto.setCreditCongeId(entity.getCreditConge() != null ? entity.getCreditConge().getId() : null);
//        dto.setLastUpdateUserId(entity.getLastUpdateUser() != null ? entity.getLastUpdateUser().getId() : null);
//        dto.setCreatedAt(entity.getCreatedAt());
//        dto.setUpdatedAt(entity.getUpdatedAt());
//        return dto;
//    }
//
//    private ProvisionConge toEntity(ProvisionCongeDTO dto,
//                                    Employe employe,
//                                    ContratEmploye contratEmploye,
//                                    Company company,
//                                    CreditConge creditConge,
//                                    User lastUpdateUser) {
//        ProvisionConge entity = new ProvisionConge();
//        entity.setId(dto.getId());
//        entity.setMoisProvisionConge(dto.getMoisProvisionConge());
//        entity.setNombreJourAccorde(dto.getNombreJourAccorde());
//        entity.setMontantProvisionConge(dto.getMontantProvisionConge());
//        entity.setEmploye(employe);
//        entity.setContratEmploye(contratEmploye);
//        entity.setCompany(company);
//        entity.setCreditConge(creditConge);
//        entity.setLastUpdateUser(lastUpdateUser);
//        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
//        entity.setUpdatedAt(LocalDateTime.now());
//        return entity;
//    }
//}
