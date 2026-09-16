package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.CreateEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.users.EmployeSansCompteDTO;
import com.tpc.tpcgestpaie.localapp.dto.users.EmployesSansCompteParEntrepriseDTO;
import com.tpc.tpcgestpaie.localapp.helper.EmployeHelper;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
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
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeHelper employeHelper;

    public EmployeService(EmployeRepository employeRepository, ContratEmployeRepository contratEmployeRepository, EmployeHelper employeHelper) {
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeHelper = employeHelper;
    }

    public boolean existsById(Long id) {
        return employeRepository.existsById(id);
    }

    public Employe save(Employe employe) {

        if (existsByMatricule(employe.getMatricule())) {
            throw new IllegalArgumentException("Ce matricule existe déjà.");
        }

        return employeRepository.save(employe);
    }

    public Employe update(Long id, CreateEmployeRequestDTO dto, User currentUser) {

        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        if (dto.matricule() != null) {
            employe.setMatricule(dto.matricule());
        }

        if (dto.titre() != null) {
            employe.setTitre(dto.titre());
        }

        if (dto.nom() != null) {
            employe.setNom(dto.nom());
        }

        if (dto.prenom() != null) {
            employe.setPrenom(dto.prenom());
        }

        if (dto.date_naissance() != null) {
            employe.setDate_naissance(dto.date_naissance());
        }

        if (dto.lieu_naissance() != null) {
            employe.setLieu_naissance(dto.lieu_naissance());
        }

        if (dto.sexe() != null) {
            employe.setSexe(dto.sexe());
        }

        if (dto.situationMatrimoniale() != null) {
            employe.setSituationMatrimoniale(dto.situationMatrimoniale());
        }

        if (dto.telephone() != null) {
            employe.setTelephone(dto.telephone());
        }

        if (dto.email() != null) {
            employe.setEmail(dto.email());
        }

        if (dto.quartier() != null) {
            employe.setQuartier(dto.quartier());
        }

        if (dto.nationalite() != null) {
            employe.setNationalite(dto.nationalite());
        }

        if (dto.numero_ifu() != null) {
            employe.setNumeroIfu(dto.numero_ifu());
        }

        if (dto.numero_cnss() != null) {
            employe.setNumero_cnss(dto.numero_cnss());
        }

        if (dto.profession() != null) {
            employe.setProfession(dto.profession());
        }

        employe.setUpdated_by(currentUser);

        return employeRepository.save(employe);
    }
    // === MÉTHODES PAGINÉES ===

    // Tous les employés avec pagination
    @Transactional
    public Page<Employe> findAll(Pageable pageable) {
        return employeRepository.findAll(pageable);
    }

    // Employés par entreprise avec pagination
    @Transactional()
    public Page<Employe> findEmployeesByCompany(Long companyId, Pageable pageable) {
        return employeRepository.findAllByCompanyId(companyId, pageable);
    }


    // Employés par entreprise avec pagination
    @Transactional()
    public Page<Employe> findAllEmployeesByCompany(Long companyId, Pageable pageable) {
        return employeRepository.findAllByCompany(companyId, pageable);   }

    // Méthode paginée pour récupérer les employés d'une entreprise (avec relations nécessaires)
    public Page<EmployeDTO> getEmployesByCompanyNumerisation(Long companyId, String search, Pageable pageable) {

        Page<Employe> employesPage;

        if (search == null || search.trim().isEmpty()) {
            // Sans recherche
            employesPage = employeRepository.findAllByCompanyWithAllFetch(companyId, pageable);
        } else {
            // Avec recherche
            employesPage = employeRepository.searchByCompanyWithAllFetch(
                    companyId,
                    "%" + search.toLowerCase() + "%",
                    pageable
            );
        }

        return employesPage.map(employe -> {
            if (employe.getCompany() != null) {
                employe.getCompany().getId(); // Force l'initialisation
            }

            return EmployeDTO.fromEntity(employe);
        });
    }



    // Employés sans contrat avec pagination
    @Transactional
    public Page<Employe> getEmployesSansContrat(Pageable pageable) {
        return employeRepository.findAllEmployesSansContrat(pageable);
    }

    // Employés récents avec pagination
    public Page<Employe> getAllEmployesRecents(Pageable pageable) {
        return employeRepository.findAllByOrderByCreated_atDesc(pageable);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Transactional()
    public List<Employe> findEmployeesByCompany(Long companyId) {
        return employeRepository.findAllByCompanyId(companyId);
    }

    public List<Employe> getEmployesSansContrat() {
        return employeRepository.findAllEmployesSansContrat();
    }

    public List<Employe> getAllEmployesRecents() {
        return employeRepository.findAllByOrderByCreated_atDesc();
    }

    public Optional<Employe> findById(Long id) {
        return employeRepository.findById(id);
    }


    public Employe findById1(Long id) {
        return employeRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        employeRepository.deleteById(id);
    }

    public boolean existsByMatricule(String matricule) {
        return employeRepository.existsByMatricule(matricule);
    }

    public List<EmployeDTO> getEmployesByEntrepriseAndDepartement(Long companyId, Long departementId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseAndDepartement(companyId, departementId)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(employe -> {
                    EmployeDTO dto = new EmployeDTO();
                    dto.setId(employe.getId());
                    dto.setNom(employe.getNom());
                    dto.setPrenom(employe.getPrenom());
                    dto.setEmail(employe.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<EmployeDTO> getEmployesByEntreprise(Long companyId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntreprise(companyId)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(employe -> {
                    EmployeDTO dto = new EmployeDTO();
                    dto.setId(employe.getId());
                    dto.setNom(employe.getNom());
                    dto.setPrenom(employe.getPrenom());
                    dto.setEmail(employe.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    /**
     * Récupère les employés dont le contrat est encours jusqu'à un mois donné
     */

    public List<EmployeDTO> getEmployesByEntrepriseAndDepartementMois(Long companyId, Long departementId, LocalDate mois) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseAndDepartementMois(companyId, departementId,mois)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(employe -> {
                    EmployeDTO dto = new EmployeDTO();
                    dto.setId(employe.getId());
                    dto.setNom(employe.getNom());
                    dto.setPrenom(employe.getPrenom());
                    dto.setEmail(employe.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<EmployeDTO> getEmployesByEntrepriseMois(Long companyId, LocalDate mois) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseMois(companyId,mois)
                .stream()
                .map(ContratEmploye::getEmploye)
                .distinct()
                .map(employe -> {
                    EmployeDTO dto = new EmployeDTO();
                    dto.setId(employe.getId());
                    dto.setNom(employe.getNom());
                    dto.setPrenom(employe.getPrenom());
                    dto.setEmail(employe.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère un employé par son ID et le mappe en DTO.
     * Retourne null si l'employé n'existe pas.
     */
    public EmployeResponseDTO findByIdAsDTO(Long id) {
        return employeRepository.findById(id)
                .map(EmployeService::mapToResponse)
                .orElse(null);
    }

    /**
     * Récupère les employés sans compte utilisateur POUR TOUTES LES ENTREPRISES ACCESSIBLES
     */
    public List<EmployesSansCompteParEntrepriseDTO> getEmployesSansCompteParEntreprise(List<Long> companyIds) {
        return companyIds.stream()
                .map(companyId -> {
                    // Récupérer les employés sans compte pour cette entreprise
                    List<Employe> employes = employeRepository.findEmployesSansCompteByCompany(companyId);

                    if (employes.isEmpty()) {
                        return null; // Ne pas inclure les entreprises sans employés sans compte
                    }

                    // Convertir en DTO
                    List<EmployeSansCompteDTO> employeDTOs = employes.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList());

                    // Récupérer le nom de l'entreprise depuis le premier employé
                    String companyName = employes.get(0).getCompany().getName();

                    return new EmployesSansCompteParEntrepriseDTO(companyId, companyName, employeDTOs);
                })
                .filter(dto -> dto != null) // Filtrer les entreprises sans employés sans compte
                .collect(Collectors.toList());
    }

    /**
     * Récupère les employés sans compte pour une entreprise spécifique
     */
    public EmployesSansCompteParEntrepriseDTO getEmployesSansCompteByCompany(Long companyId) {
        List<Employe> employes = employeRepository.findEmployesSansCompteByCompany(companyId);

        if (employes.isEmpty()) {
            return null;
        }

        List<EmployeSansCompteDTO> employeDTOs = employes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        String companyName = employes.get(0).getCompany().getName();

        return new EmployesSansCompteParEntrepriseDTO(companyId, companyName, employeDTOs);
    }

    /**
     * Convertit un Employe en DTO
     */
    private EmployeSansCompteDTO convertToDTO(Employe employe) {
        return new EmployeSansCompteDTO(
                employe.getId(),
                employe.getMatricule(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getTelephone(),
                employe.getDate_naissance()
        );
    }

    /**
     * Vérifie si un employé a déjà un compte
     */
    public boolean employeHasUserAccount(Long employeId) {
        return employeRepository.employeHasUserAccount(employeId);
    }


    public Employe mapToEmploye(CreateEmployeRequestDTO dto, User user, Company company) {
        Employe employe = new Employe();
        employe.setMatricule(dto.matricule());
        employe.setTitre(dto.titre());
        employe.setNom(dto.nom());
        employe.setPrenom(dto.prenom());
        employe.setSexe(dto.sexe());
        employe.setDate_naissance(dto.date_naissance());
        employe.setLieu_naissance(dto.lieu_naissance());
        employe.setNumeroIfu(dto.numero_ifu());
        employe.setSituationMatrimoniale(dto.situationMatrimoniale());
        employe.setNumero_cnss(dto.numero_cnss());
        employe.setTelephone(dto.telephone());
        employe.setEmail(dto.email());
        employe.setQuartier(dto.quartier());
        employe.setNationalite(dto.nationalite());
        employe.setProfession(dto.profession());
        employe.setCompany(company);

        employe.setCreated_at(LocalDateTime.now());
        employe.setAdded_by(user);

        return employe;
    }


    public Employe mapToEmploye(CreateEmployeRequestDTO dto) {
        Employe employe = new Employe();
        employe.setMatricule(dto.matricule());
        employe.setTitre(dto.titre());
        employe.setNom(dto.nom());
        employe.setPrenom(dto.prenom());
        employe.setSexe(dto.sexe());
        employe.setDate_naissance(dto.date_naissance());
        employe.setLieu_naissance(dto.lieu_naissance());
        employe.setNumeroIfu(dto.numero_ifu());
        employe.setSituationMatrimoniale(dto.situationMatrimoniale());
        employe.setNumero_cnss(dto.numero_cnss());
        employe.setTelephone(dto.telephone());
        employe.setEmail(dto.email());
        employe.setQuartier(dto.quartier());
        employe.setNationalite(dto.nationalite());
        employe.setProfession(dto.profession());
        employe.setCreated_at(LocalDateTime.now());
        return employe;
    }


    public static EmployeResponseDTO mapToResponse(Employe emp) {

        return new EmployeResponseDTO(
                emp.getId(),
                emp.getMatricule(),
                emp.getTitre(),
                emp.getNom(),
                emp.getPrenom(),
                emp.getDate_naissance(),
                emp.getLieu_naissance(),
                emp.getSexe(),
                emp.getSituationMatrimoniale(),
                emp.getNumeroIfu(),
                emp.getTelephone(),
                emp.getEmail(),
                emp.getNom_pere(),
                emp.getNom_mere(),
                emp.getBoite_postale(),
                emp.getMaison(),
                emp.getNumeroCarre(),
                emp.getQuartier(),
                emp.getNationalite(),
                emp.getNumeroCnss(),
                emp.getProfession(),
                emp.isIs_employe_interne(),

                emp.getCompany() != null ? emp.getCompany().getId() : null,
                emp.getCompany() != null ? emp.getCompany().getName() : null,

                emp.getDate_naissance() != null ? emp.getAge() : null,
                emp.getDate_naissance() != null ? emp.getProchainAnniversaire() : null,
                emp.getDate_naissance() != null ? emp.getJoursRestants() : null,

                emp.getCreated_at(),
                emp.getUpdated_at()
        );
    }
}