package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MensualiteDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.MensualiteRepository;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.Status;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MensualiteService {

    private final MensualiteRepository mensualiteRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final InstitutionRepository institutionRepository;
    private final UserService userService;

    public MensualiteService(MensualiteRepository mensualiteRepository,
                             ContratEmployeRepository contratEmployeRepository,
                             EmployeRepository employeRepository,
                             CompanyRepository companyRepository,
                             InstitutionRepository institutionRepository,
                             UserService userService) {
        this.mensualiteRepository = mensualiteRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
        this.institutionRepository = institutionRepository;
        this.userService = userService;
    }

    public List<MensualiteDTO> findAll() {
        return mensualiteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<MensualiteDTO> findById(Long id) {
        return mensualiteRepository.findById(id).map(this::toDTO);
    }

    public boolean existsById(Long id) {
        return mensualiteRepository.existsById(id);
    }


    @Transactional
    public List<MensualiteDTO> findByCompanyId(Long companyId) {
        return mensualiteRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MensualiteDTO save(MensualiteDTO dto) {
        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));

        Employe employe = contratEmploye.getEmploye();
        if (employe == null) {
            throw new RuntimeException("Employé introuvable dans le contrat");
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));

        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution introuvable"));

        User user = userService.getCurrentUser();

        Mensualite mensualite = toEntity(dto, contratEmploye, employe, company, institution, user);
        mensualite.setStatut(Status.IN_PROCESS);
        Mensualite saved = mensualiteRepository.save(mensualite);
        return toDTO(saved);
    }

    @Transactional
    public MensualiteDTO setEstSoldee(Long mensualiteId) {
        Mensualite mensualite = mensualiteRepository.findById(mensualiteId)
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable avec l'ID : " + mensualiteId));

        mensualite.setMensualiteSolde(true);
        mensualite.setStatut(Status.SOLDEE);
        mensualite.setDateSoldee(LocalDateTime.now());
        mensualite= mensualiteRepository.save(mensualite);
        return toDTO(mensualite);
    }

    @Transactional
    public MensualiteDTO restauterSolde(Long mensualiteId) {
        Mensualite mensualite = mensualiteRepository.findById(mensualiteId)
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable avec l'ID : " + mensualiteId));
        mensualite.setMensualiteSolde(false);
        mensualite.setStatut(Status.IN_PROCESS);
        mensualite.setDateSoldee(LocalDateTime.now());
        mensualite= mensualiteRepository.save(mensualite);
        return toDTO(mensualite);
    }

    @Transactional
    public List<MensualiteDTO> loadMensualiteIfNotSoldee( Long companyId, String mois) {
        List<Mensualite> mensualites = mensualiteRepository
                .findByContratEmployeIdAndCompanyIdAndMoisAndStatutNotSoldee(companyId, mois);

        return mensualites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MensualiteDTO annule(Long mensualiteId,String motif) {
        Mensualite mensualite = mensualiteRepository.findById(mensualiteId)
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable avec l'ID : " + mensualiteId));
        mensualite.setMensualiteSolde(false);
        mensualite.setStatut(Status.CANCELLED);
        mensualite.setMotifAnnulation(motif);
        mensualite= mensualiteRepository.save(mensualite);
        return toDTO(mensualite);
    }

    @Transactional
    public MensualiteDTO update(Long id, MensualiteDTO dto) {
        Mensualite existing = mensualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensualité introuvable"));

        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));

        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution introuvable"));
        User user = userService.getCurrentUser();

        // Met à jour les champs de l'entité existante
        existing.setDureeMensualite(dto.getDureeMensualite());
        existing.setMoisDemarrage(dto.getMoisDemarrage());
        existing.setMoisFin(dto.getMoisFin());
        existing.setMontantMensue(dto.getMontantMensuel());
        existing.setMensualiteSolde(dto.isMensualiteSolde());

        existing.setEmploye(employe);
        existing.setContratEmploye(contratEmploye);
        existing.setCompany(company);
        existing.setInstitution(institution);
        existing.setLastUpdateUser(user);

        existing.setUpdatedAt(LocalDateTime.now());

        Mensualite saved = mensualiteRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        mensualiteRepository.deleteById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Mensualite mensualite = mensualiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensualité introuvable"));
        mensualite.softDelete();
        mensualiteRepository.save(mensualite);
    }

    // === Mapping ===

    private MensualiteDTO toDTO(Mensualite entity) {
        MensualiteDTO dto = new MensualiteDTO();
        dto.setId(entity.getId());
        dto.setDureeMensualite(entity.getDureeMensualite());
        dto.setMoisDemarrage(entity.getMoisDemarrage());
        dto.setMoisFin(entity.getMoisFin());
        dto.setMontantMensuel(entity.getMontantMensue());
        dto.setMensualiteSolde(entity.isMensualiteSolde());

        dto.setStatut(entity.getStatut());
        dto.setDateSoldee(entity.getDateSoldee());
        dto.setMotifAnnulation(entity.getMotifAnnulation());


        dto.setInstitutionId(entity.getInstitution() != null ? entity.getInstitution().getId() : null);
        dto.setInstitution(entity.getInstitution());
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));
        dto.setLastUpdateUserId(entity.getLastUpdateUser() != null ? entity.getLastUpdateUser().getId() : null);

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeletedAt(entity.getDeletedAt());

        return dto;
    }

    public Mensualite toEntity(MensualiteDTO dto,
                               ContratEmploye contratEmploye,
                               Employe employe,
                               Company company,
                               Institution institution,
                               User lastUpdateUser) {
        Mensualite entity = new Mensualite();

        entity.setId(dto.getId());
        entity.setDureeMensualite(dto.getDureeMensualite());
        entity.setMoisDemarrage(dto.getMoisDemarrage());
        entity.setMoisFin(dto.getMoisFin());
        entity.setMontantMensue(dto.getMontantMensuel());
        entity.setMensualiteSolde(dto.isMensualiteSolde());

        entity.setMotifAnnulation(dto.getMotifAnnulation());
        entity.setStatut(dto.getStatut());
        entity.setDateSoldee(dto.getDateSoldee());
        entity.setDeletedAt(dto.getDeletedAt());

        entity.setContratEmploye(contratEmploye);
        entity.setEmploye(employe);
        entity.setCompany(company);
        entity.setInstitution(institution);
        entity.setLastUpdateUser(lastUpdateUser);

        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    public Mensualite toEntity(MensualiteDTO dto) {
        return toEntity(dto, null, null, null, null, null);
    }

    private EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

    private ContractEmployeDTO convertContratEmployeToDTO(ContratEmploye contratEmploye) {
        if (contratEmploye == null) return null;
        ContractEmployeDTO dto = new ContractEmployeDTO();
        dto.setId(contratEmploye.getId());
        dto.setDepartement(contratEmploye.getDepartement());


//        dto.setDepartementDTO(DepartementDTO.fromEntity1(contratEmploye.getDepartement()));
        return dto;
    }

    public List<MensualiteDTO> getMensualitesParEmploye(Long employeId) {
        List<Mensualite> mensualites = mensualiteRepository.findByEmployeId(employeId);
        return mensualites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
