package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.paie.PrelevementMensualiteDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.PrelevementMensualiteListResponse;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.MensualiteRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.PrelevementMensualiteRepository;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PrelevementMensualiteService {

    private final PrelevementMensualiteRepository prelevementMensualiteRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final InstitutionRepository institutionRepository;
    private final MensualiteRepository mensualiteRepository;
    private final UserService userService;

    public PrelevementMensualiteService(PrelevementMensualiteRepository prelevementMensualiteRepository,
                                        EmployeRepository employeRepository,
                                        ContratEmployeRepository contratEmployeRepository,
                                        CompanyRepository companyRepository,
                                        InstitutionRepository institutionRepository,
                                        MensualiteRepository mensualiteRepository,
                                        UserService userService) {
        this.prelevementMensualiteRepository = prelevementMensualiteRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.institutionRepository = institutionRepository;
        this.mensualiteRepository = mensualiteRepository;
        this.userService = userService;
    }

    public List<PrelevementMensualiteDTO> findAll() {
        return prelevementMensualiteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrelevementMensualiteDTO> findById(Long id) {
        return prelevementMensualiteRepository.findById(id).map(this::toDTO);
    }

    public boolean existsById(Long id) {
        return prelevementMensualiteRepository.existsById(id);
    }

    public List<PrelevementMensualiteDTO> findByCompanyId(Long companyId) {
        return prelevementMensualiteRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PrelevementMensualiteDTO> findByMois(String mois) {
        return prelevementMensualiteRepository.findByMoisPrelevement(mois)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PrelevementMensualiteDTO> findByMoisAndInstitution(String mois, Long institutionId) {
        return prelevementMensualiteRepository.findByMoisPrelevementAndInstitutionId(mois, institutionId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PrelevementMensualiteDTO> findByMoisAndCompany(String mois, Long companyId) {
        return prelevementMensualiteRepository.findByMoisPrelevementAndCompanyId(mois, companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PrelevementMensualiteDTO> findByMoisCompanyAndInstitution(String mois, Long companyId, Long institutionId) {
        return prelevementMensualiteRepository.findByMoisPrelevementAndCompanyIdAndInstitutionId(mois, companyId, institutionId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrelevementMensualiteDTO save(PrelevementMensualiteDTO dto) {
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution introuvable"));
        Mensualite mensualite = mensualiteRepository.findById(dto.getMensualiteId())
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable"));
        User user = userService.getCurrentUser();

        PrelevementMensualite entity = toEntity(dto, employe, contratEmploye, company, institution, mensualite, user);
        PrelevementMensualite saved = prelevementMensualiteRepository.save(entity);

        return toDTO(saved);
    }

    @Transactional
    public void deleteById(Long id) {
        prelevementMensualiteRepository.deleteById(id);
    }

    @Transactional
    public boolean save(PrelevementMensualite pm){
        try {
            pm.setDatePrelevement(LocalDateTime.now().toLocalDate());
            pm.setLastUpdateUser(userService.getCurrentUser());
            pm.setCreatedAt(LocalDateTime.now());
            pm.setUpdatedAt(LocalDateTime.now());
            prelevementMensualiteRepository.save(pm);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public PrelevementMensualiteDTO update(Long id, PrelevementMensualiteDTO dto) {
        PrelevementMensualite existing = prelevementMensualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prélèvement introuvable"));

        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution introuvable"));
        Mensualite mensualite = mensualiteRepository.findById(dto.getMensualiteId())
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable"));
        User user = userService.getCurrentUser();

        existing.setDatePrelevement(dto.getDatePrelevement());
        existing.setMoisPrelevement(dto.getMoisPrelevement());
        existing.setMontant(dto.getMontant());
        existing.setEmploye(employe);
        existing.setContratEmploye(contratEmploye);
        existing.setCompany(company);
        existing.setInstitution(institution);
        existing.setMensualite(mensualite);
        existing.setLastUpdateUser(user);
        existing.setUpdatedAt(LocalDateTime.now());

        PrelevementMensualite saved = prelevementMensualiteRepository.save(existing);
        return toDTO(saved);
    }

    @Transactional
    public PrelevementMensualite findByMoisPrelevementAndContratEmployeIdAndCompanyIdAndInstitutionId(
            String moisPrelevement,Long companyId,Long institutionId,Long contratEmployeId
    ) {
        return prelevementMensualiteRepository
                .findOneByMoisPrelevementAndContratEmployeIdAndCompanyIdAndInstitutionId(
                        moisPrelevement,
                        companyId,
                        institutionId,
                        contratEmployeId
                );
    }

    public List<PrelevementMensualite> findByMoisCompanyAndOptionalInstitution(String mois, Long companyId, Long institutionId) {
        if (institutionId != null) {
            return prelevementMensualiteRepository
                    .findByMoisPrelevementAndCompanyIdAndInstitutionId(mois, companyId, institutionId);
        } else {
            return prelevementMensualiteRepository
                    .findByMoisPrelevementAndCompanyId(mois, companyId);
        }
    }

    public PrelevementMensualiteListResponse getPrelevementMensualiteListResponse(String mois, Long companyId, Long institutionId) {
        List<PrelevementMensualite> prelevements;

        if (institutionId == null) {
            prelevements = prelevementMensualiteRepository.findByMoisPrelevementAndCompanyId(mois, companyId);
        } else {
            Institution institution = institutionRepository.findById(institutionId)
                    .orElseThrow(() -> new RuntimeException("Institution non trouvée"));
            prelevements = prelevementMensualiteRepository.findByMoisPrelevementAndCompanyIdAndInstitutionId(mois, companyId, institution.getId());
        }

        // Conversion en DTO
        List<PrelevementMensualiteDTO> dtos = prelevements.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // Calcul du total (somme des montants)
        double total = prelevements.stream()
                .mapToDouble(p -> p.getMontant().doubleValue())
                .sum();

        PrelevementMensualiteListResponse response = new PrelevementMensualiteListResponse();
        response.setMois(mois);
        response.setCompanyId(companyId);
        response.setInstitutionId(institutionId);
        response.setPrelevements(dtos);
        response.setTotal(total);

        return response;
    }



    // === Mapping ===

    public PrelevementMensualiteDTO toDTO(PrelevementMensualite entity) {
        PrelevementMensualiteDTO dto = new PrelevementMensualiteDTO();
        dto.setId(entity.getId());
        dto.setDatePrelevement(entity.getDatePrelevement());
        dto.setMoisPrelevement(entity.getMoisPrelevement());
        dto.setMontant(entity.getMontant());

        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);

        if(entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmployeNomComplet(entity.getEmploye().getNom() + " " + entity.getEmploye().getPrenom());
        } else {
            dto.setEmployeId(null);
            dto.setEmployeNomComplet(null);
        }

        if(entity.getInstitution() != null) {
            dto.setInstitutionId(entity.getInstitution().getId());
            dto.setInstitutionNom(entity.getInstitution().getName());
        } else {
            dto.setInstitutionId(null);
            dto.setInstitutionNom(null);
        }
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setInstitutionId(entity.getInstitution() != null ? entity.getInstitution().getId() : null);
        dto.setMensualiteId(entity.getMensualite() != null ? entity.getMensualite().getId() : null);
        dto.setLastUpdateUserId(entity.getLastUpdateUser() != null ? entity.getLastUpdateUser().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private PrelevementMensualite toEntity(PrelevementMensualiteDTO dto,
                                           Employe employe,
                                           ContratEmploye contratEmploye,
                                           Company company,
                                           Institution institution,
                                           Mensualite mensualite,
                                           User lastUpdateUser) {
        PrelevementMensualite entity = new PrelevementMensualite();
        entity.setId(dto.getId());
        entity.setDatePrelevement(dto.getDatePrelevement());
        entity.setMoisPrelevement(dto.getMoisPrelevement());
        entity.setMontant(dto.getMontant());
        entity.setEmploye(employe);
        entity.setContratEmploye(contratEmploye);
        entity.setCompany(company);
        entity.setInstitution(institution);
        entity.setMensualite(mensualite);
        entity.setLastUpdateUser(lastUpdateUser);
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
