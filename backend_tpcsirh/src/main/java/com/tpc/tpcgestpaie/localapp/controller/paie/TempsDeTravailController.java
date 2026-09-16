package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.TempsDeTravailDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.TempsDeTravail;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.TempsDeTravailRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.paie.TempsDeTravailService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/paie/temps_de_travail")
public class TempsDeTravailController {

    private final UserService userService;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final TempsDeTravailService tempsDeTravailService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final TempsDeTravailRepository tempsDeTravailRepository;
    private final CompanyRepository companyRepository;

    public TempsDeTravailController(UserService userService, CompanyService companyService, EmployeService employeService, ContratEmployeService contratEmployeService, TempsDeTravailService tempsDeTravailService, ContratEmployeRepository contratEmployeRepository, TempsDeTravailRepository tempsDeTravailRepository, CompanyRepository companyRepository) {
        this.userService = userService;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.tempsDeTravailService = tempsDeTravailService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.tempsDeTravailRepository = tempsDeTravailRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<TempsDeTravailDTO> tempsDeTravailDTOS = tempsDeTravailService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Temps de travail récupéré avec succès", tempsDeTravailDTOS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération du temps de travaiil", null));
        }
    }

    @GetMapping("/entreprise/{idCompany}")
    public ResponseEntity<?> getEmployesTempsTravailParEntreprise(
            @PathVariable Long idCompany,
            @RequestParam(required = false) Long idDepartement,
            @RequestParam String mois
    ) {
        try {
            Company company = companyService.getById(idCompany);
            if (company == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            List<EmployeDTO> employes;
            if (idDepartement != null) {
                employes = employeService.getEmployesByEntrepriseAndDepartement(company.getId(), idDepartement);
            } else {
                employes = employeService.getEmployesByEntreprise(company.getId());
            }

            List<TempsDeTravailDTO> result = employes.stream()
                    .map(employe -> tempsDeTravailService.getByEmployeAndMois(employe.getId(), mois)
                            .orElseGet(() -> {
                                TempsDeTravailDTO emptyDto = new TempsDeTravailDTO();
                                emptyDto.setEmployeId(employe.getId());
                                emptyDto.setEmploye(employe);  // j'imagine que employe est déjà un EmployeDTO
                                emptyDto.setMois(mois);

                                // 👉 Ici tu récupères le contrat actif
                                ContractEmployeDTO contratActif = contratEmployeService.getActifDTOByEmployeId(employe.getId())
                                        .orElse(null);
                                emptyDto.setContratEmploye(contratActif);
                                if (contratActif != null) {
                                    emptyDto.setContratEmployeId(contratActif.getId());
                                    emptyDto.setCompanyId(contratActif.getCompanyId());
                                }

                                return emptyDto;
                            })
                    )
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Données récupérées avec succès", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }

    @Transactional
    @PostMapping("/entreprise/{idCompany}/temps-travail")
    public ResponseEntity<?> enregistrerTempsTravailPourTous(
            @PathVariable Long idCompany,
            @RequestParam String mois,
            @RequestBody List<TempsDeTravailDTO> tempsDeTravailList
    ) {
        try {
            Company company = companyService.getById(idCompany);
            if (company == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            List<TempsDeTravailDTO> result = new ArrayList<>();

            for (TempsDeTravailDTO dto : tempsDeTravailList) {
                Optional<Employe> optEmploye = employeService.findById(dto.getEmployeId());

                if (optEmploye.isEmpty()) {
                    System.out.println("❗ Employé " + dto.getEmployeId() + " introuvable.");
                    continue;
                }

                Employe employe = optEmploye.get();

                List<ContratEmploye> contratsActifs = contratEmployeRepository.findActiveByEmployeWithAllJoinsOrdered(employe);

// Trouver le contrat actif lié à l'entreprise
                Optional<ContratEmploye> contratLieAEntreprise = contratsActifs.stream()
                        .filter(c -> c.getCompany() != null && c.getCompany().getId().equals(idCompany))
                        .findFirst();

                if (contratLieAEntreprise.isEmpty()) {
                    System.out.println("❗ Employé " + dto.getEmployeId() + " n'a pas de contrat actif avec l'entreprise " + idCompany);
                    continue;
                }

// Récupérer l'id du contrat actif
                Long contratActifId = contratLieAEntreprise.get().getId();
                System.out.println("✅ id actif " + contratActifId);
                dto.setMois(mois);
                dto.setContratEmployeId(contratActifId);
                dto.setEmploye(EmployeDTO.fromEntity(employe));
                dto.setCompanyId(convertCompanyToDTO(company).getId());

                // Vérifier existence et mettre à jour si nécessaire
                Optional<TempsDeTravail> existingOpt = tempsDeTravailRepository.findByEmployeIdAndMois(employe.getId(), mois);

                if (existingOpt.isPresent()) {
                    TempsDeTravail existing = existingOpt.get();

                    boolean besoinDeUpdate = existing.getNombreJour() != dto.getNombreJour()
                            || !existing.getMois().equals(dto.getMois()); // ici mois est String donc ok avec equals

                    if (besoinDeUpdate) {
                        TempsDeTravailDTO saved = tempsDeTravailService.update(existing, dto);
                        result.add(saved);
                        System.out.println("✅ Temps de travail mis à jour pour employé " + employe.getId());
                    } else {
                        System.out.println("ℹ Temps de travail identique, pas de mise à jour pour employé " + employe.getId());
                        result.add(tempsDeTravailService.convertToDTO(existing));
                    }
                } else {
                    // Nouveau temps de travail
                    TempsDeTravailDTO saved = tempsDeTravailService.save(dto);
                    result.add(saved);
                    System.out.println("✅ Temps de travail créé pour employé " + employe.getId());
                }
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Temps de travail enregistrés", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement : " + e.getMessage(), null));
        }
    }

    @GetMapping("/entreprise/{idCompany}/liste")
    public ResponseEntity<?> getByCompany(@PathVariable Long idCompany) {
        try {
            // Vérifier l'existence de l'entreprise
            Optional<Company> optionalCompany = companyRepository.findById(idCompany);
            if (optionalCompany.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée avec l'id : " + idCompany, null));
            }

            // Si l'entreprise existe, récupérer les temps de travail
            List<TempsDeTravailDTO> dtos = tempsDeTravailService.findByCompanyId(idCompany);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Temps de travail récupéré avec succès", dtos)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération du temps de travail", null));
        }
    }




    @GetMapping("/entreprise/{idCompany}/departement/{idDepartement}/liste")
    public ResponseEntity<?> getByCompanyAndDepartement(
            @PathVariable Long idCompany,
            @PathVariable Long idDepartement
    ) {
        try {
            List<TempsDeTravailDTO> dtos = tempsDeTravailService.findByCompanyIdAndDepartementId(idCompany, idDepartement);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Temps de travail récupéré avec succès", dtos)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération du temps de travail", null));
        }
    }

    private CompanyDTO convertCompanyToDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        return dto;
    }



}