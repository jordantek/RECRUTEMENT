package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.HeureSupplementaireDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.HeureSupplementaire;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.HeureSupplementaireRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HeureSupplementaireService {

    private final HeureSupplementaireRepository repository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public HeureSupplementaireService(
            HeureSupplementaireRepository repository,
            EmployeRepository employeRepository,
            ContratEmployeRepository contratEmployeRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    private HeureSupplementaireDTO mapToDTO(HeureSupplementaire hs) {
        HeureSupplementaireDTO dto = new HeureSupplementaireDTO();

        dto.setId(hs.getId());
        dto.setDate(hs.getDate());
        dto.setMois(hs.getMois());
        dto.setSalaireBaseContrat(hs.getSalaireBaseContrat());
        dto.setSalaireBrutContrat(hs.getSalaireBrutContrat());
        dto.setHeures12(hs.getHeures12());
        dto.setHeures35(hs.getHeures35());
        dto.setHeures50(hs.getHeures50());
        dto.setHeures100(hs.getHeures100());
        dto.setTotalHeures(hs.getTotalHeures());
        dto.setMajoration12(hs.getMajoration12());
        dto.setMajoration35(hs.getMajoration35());
        dto.setMajoration50(hs.getMajoration50());
        dto.setMajoration100(hs.getMajoration100());
        dto.setMontant(hs.getMontant());
        dto.setObservation(hs.getObservation());

        dto.setContratEmployeId(hs.getContratEmploye().getId());
        dto.setEmployeId(hs.getEmploye().getId());
        dto.setCompanyId(hs.getCompany().getId());
        dto.setLastUpdateUserId(hs.getLastUpdateUser().getId());

        // Mapping complet de l'employé
        Employe employe = hs.getEmploye();
        EmployeDTO employeDTO = new EmployeDTO();
        employeDTO.setId(employe.getId());
        employeDTO.setNom(employe.getNom());
        employeDTO.setPrenom(employe.getPrenom());
        dto.setEmploye(employeDTO);

        dto.setCreatedAt(hs.getCreatedAt());
        dto.setUpdatedAt(hs.getUpdatedAt());

        return dto;
    }

    @Transactional
    public HeureSupplementaireDTO save(HeureSupplementaireDTO dto) {
        HeureSupplementaire hs = new HeureSupplementaire();

        hs.setDate(dto.getDate());
        hs.setMois(dto.getMois());
        hs.setSalaireBaseContrat(dto.getSalaireBaseContrat());
        hs.setSalaireBrutContrat(dto.getSalaireBrutContrat());
        hs.setHeures12(dto.getHeures12());
        hs.setHeures35(dto.getHeures35());
        hs.setHeures50(dto.getHeures50());
        hs.setHeures100(dto.getHeures100());
        hs.setTotalHeures(dto.getTotalHeures());
        hs.setMajoration12(dto.getMajoration12());
        hs.setMajoration35(dto.getMajoration35());
        hs.setMajoration50(dto.getMajoration50());
        hs.setMajoration100(dto.getMajoration100());
        hs.setMontant(dto.getMontant());
        hs.setObservation(dto.getObservation());

        hs.setContratEmploye(contratEmployeRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new EntityNotFoundException("ContratEmploye introuvable")));
        hs.setEmploye(employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new EntityNotFoundException("Employé introuvable")));
        hs.setCompany(companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company introuvable")));
        hs.setLastUpdateUser(userRepository.findById(dto.getLastUpdateUserId())
                .orElseThrow(() -> new EntityNotFoundException("User introuvable")));

        return mapToDTO(repository.save(hs));
    }

    @Transactional
    public HeureSupplementaireDTO update(HeureSupplementaireDTO dto) {
        HeureSupplementaire hs = repository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("HeureSup introuvable"));

        hs.setDate(dto.getDate());
        hs.setMois(dto.getMois());
        hs.setSalaireBaseContrat(dto.getSalaireBaseContrat());
        hs.setSalaireBrutContrat(dto.getSalaireBrutContrat());
        hs.setHeures12(dto.getHeures12());
        hs.setHeures35(dto.getHeures35());
        hs.setHeures50(dto.getHeures50());
        hs.setHeures100(dto.getHeures100());
        hs.setMajoration12(dto.getMajoration12());
        hs.setMajoration35(dto.getMajoration35());
        hs.setMajoration50(dto.getMajoration50());
        hs.setMajoration100(dto.getMajoration100());
        hs.setTotalHeures(dto.getTotalHeures());
        hs.setMontant(dto.getMontant());
        hs.setObservation(dto.getObservation());

        hs.setLastUpdateUser(userRepository.findById(dto.getLastUpdateUserId())
                .orElseThrow(() -> new EntityNotFoundException("User introuvable")));

        return mapToDTO(repository.save(hs));
    }

    public Optional<HeureSupplementaireDTO> findById(Long id) {
        return repository.findById(id).map(this::mapToDTO);
    }

    public List<HeureSupplementaireDTO> findAll() {
        return repository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public List<HeureSupplementaireDTO> findByEntrepriseId(Long entrepriseId) {
        return repository.findAllByCompany_Id(entrepriseId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public List<HeureSupplementaireDTO> findByEntrepriseIdAndMois(Long entrepriseId, String mois) {
        return repository.findAllByCompanyIdAndMois(entrepriseId,mois).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public void delete(Long id) {
        HeureSupplementaire hs = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HeureSup non trouvée"));
        hs.softDelete();
        repository.save(hs);
    }

    public boolean existePourEmployeEtMois(Long employeId, String mois) {
        return repository.findByEmployeIdAndMois(employeId, mois).isPresent();
    }


}
