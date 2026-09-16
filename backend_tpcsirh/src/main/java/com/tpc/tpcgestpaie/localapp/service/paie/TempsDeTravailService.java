package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.DepartementDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.TempsDeTravailDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.TempsDeTravail;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.TempsDeTravailRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TempsDeTravailService {

    private final TempsDeTravailRepository tempsDeTravailRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public TempsDeTravailService(
            TempsDeTravailRepository tempsDeTravailRepository,
            EmployeRepository employeRepository,
            ContratEmployeRepository contratEmployeRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.tempsDeTravailRepository = tempsDeTravailRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<TempsDeTravailDTO> findAll() {
        return tempsDeTravailRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TempsDeTravailDTO> findById(Long id) {
        return tempsDeTravailRepository.findById(id).map(this::toDTO);
    }

//    @Transactional
//    public TempsDeTravailDTO save(TempsDeTravailDTO dto) {
//        TempsDeTravail entity = toEntity(dto);
//
//        // Vérifier si l'employé a un contrat actif
//        List<ContratEmploye> actif = contratEmployeRepository.findByEmployeWithAllJoinsOrdered(entity.getEmploye());
//        if (actif == null || actif.isEmpty()) {
//            throw new IllegalArgumentException("L'employé n'a pas de contrat actif.");
//        }
//
//        return toDTO(tempsDeTravailRepository.save(entity));
//    }
//

//    @Transactional
//    public TempsDeTravailDTO save(TempsDeTravailDTO dto, Employe employe) {
//        TempsDeTravail entity = toEntity(dto);
//        entity.setEmploye(employe);
//
//        List<ContratEmploye> contratsActifs = contratEmployeRepository.findActiveByEmployeWithAllJoinsOrdered(employe);
//        if (contratsActifs == null || contratsActifs.isEmpty()) {
//            throw new IllegalArgumentException("L'employé n'a pas de contrat actif.");
//        }
//
//        TempsDeTravail saved = tempsDeTravailRepository.save(entity);
//
//        return toDTO(saved);  // toDTO reste privée ici, c'est OK
//    }

    @Transactional
    public TempsDeTravailDTO save(TempsDeTravailDTO dto) {
        TempsDeTravail entity = toEntity(dto);

        List<ContratEmploye> actif = contratEmployeRepository.findActiveByEmployeWithAllJoinsOrdered(entity.getEmploye());
        System.out.println("✔ Temps nombre : " + actif.size());
        if (actif == null || actif.isEmpty()) {
            throw new IllegalArgumentException("L'employé n'a pas de contrat actif.");
        }

        return toDTO(tempsDeTravailRepository.save(entity));
    }

    @Transactional
    public TempsDeTravailDTO update(TempsDeTravail existing, TempsDeTravailDTO dto) {
        existing.setNombreJour(dto.getNombreJour());
        existing.setMois(dto.getMois());
        // mets à jour d'autres champs si besoin

        TempsDeTravail saved = tempsDeTravailRepository.save(existing);
        return toDTO(saved); // méthode privée, OK ici
    }


    public void delete(Long id) {
        tempsDeTravailRepository.deleteById(id);
    }

    public List<TempsDeTravailDTO> findByCompanyId(Long companyId) {
        return tempsDeTravailRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TempsDeTravailDTO> findByCompanyIdAndDepartementId(Long companyId, Long departementId) {
        return tempsDeTravailRepository.findAllByCompanyAndDepartement(companyId, departementId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    public List<TempsDeTravailDTO> findByEmployeId(Long employeId) {
        return tempsDeTravailRepository.findByEmployeId(employeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TempsDeTravailDTO> getByEmployeAndMois(Long employeId, String mois) {
        return tempsDeTravailRepository.findByEmployeIdAndMois(employeId, mois)
                .map(this::toDTO);
    }

    public List<EmployeDTO> findEmployesNonArretesByCompany(Long companyId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntreprise(companyId)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(this::convertEmployeToDTO)
                .collect(Collectors.toList());
    }
    public List<TempsDeTravailDTO> getByCompanyAndMois(Long companyId, String mois) {
        return tempsDeTravailRepository.findByCompanyIdAndMois(companyId,mois)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

    public List<EmployeDTO> findEmployesNonArretesByCompanyAndDepartement(Long companyId, Long departementId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseAndDepartement(companyId, departementId)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(this::convertEmployeToDTO)
                .collect(Collectors.toList());
    }

    // === Mapping ===

    public TempsDeTravailDTO convertToDTO(TempsDeTravail entity) {
        return toDTO(entity);
    }

    private TempsDeTravailDTO toDTO(TempsDeTravail entity) {
        TempsDeTravailDTO dto = new TempsDeTravailDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setMois(entity.getMois());
        dto.setNombreJour(entity.getNombreJour());

        if (entity.getEmploye() != null) {
            dto.setEmployeId(entity.getEmploye().getId());
            dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));
        }

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
            dto.setCompany(convertCompanyToDTO(entity.getCompany()));
        }

        if (entity.getContratEmploye() != null) {
            dto.setContratEmployeId(entity.getContratEmploye().getId());
            dto.setContratEmploye(convertContratEmployeToDTO(entity.getContratEmploye()));
        }

        return dto;
    }

    private TempsDeTravail toEntity(TempsDeTravailDTO dto) {
        TempsDeTravail entity = new TempsDeTravail();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setMois(dto.getMois());
        entity.setNombreJour(dto.getNombreJour());

        if (dto.getEmployeId() != null) {
            entity.setEmploye(employeRepository.findById(dto.getEmployeId()).orElse(null));
        }

        if (dto.getCompanyId() != null) {
            entity.setCompany(companyRepository.findById(dto.getCompanyId()).orElse(null));
        }

        if (dto.getContratEmployeId() != null) {
            entity.setContratEmploye(contratEmployeRepository.findById(dto.getContratEmployeId()).orElse(null));
        }

        return entity;
    }

    private EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

    private CompanyDTO convertCompanyToDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        return dto;
    }

    private ContractEmployeDTO convertContratEmployeToDTO(ContratEmploye contratEmploye) {
        if (contratEmploye == null) return null;

        ContractEmployeDTO dto = new ContractEmployeDTO();
        dto.setId(contratEmploye.getId());

       dto.setDepartement(contratEmploye.getDepartement());
        dto.setDepartementDTO(DepartementDTO.fromEntity1(contratEmploye.getDepartement()));
        return dto;
    }

}
