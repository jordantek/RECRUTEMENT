package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MontantRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MontantRubriqueRequestDto;
import com.tpc.tpcgestpaie.localapp.helper.MontantRubriqueHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.MontantRubriqueRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.paie.MontantRubriqueService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/paie/montant-rubriques")
public class MontantRubriqueController {

    private final MontantRubriqueService montantRubriqueService;
    private final MontantRubriqueHelper montantRubriqueHelper;
    private final UserService userService;
    private final CompanyService companyService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeRepository employeRepository;
    private final RubriqueRepository rubriqueRepository;
    private final RubriqueService rubriqueService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final DepartementService departementService;
    private final MontantRubriqueRepository montantRubriqueRepository;

    public MontantRubriqueController(MontantRubriqueService montantRubriqueService,
                                     MontantRubriqueHelper montantRubriqueHelper,
                                     UserService userService,
                                     CompanyService companyService,
                                     ContratEmployeRepository contratEmployeRepository,
                                     EmployeRepository employeRepository,
                                     RubriqueRepository rubriqueRepository, RubriqueService rubriqueService, EmployeService employeService, ContratEmployeService contratEmployeService, DepartementService departementService, MontantRubriqueRepository montantRubriqueRepository) {
        this.montantRubriqueService = montantRubriqueService;
        this.montantRubriqueHelper = montantRubriqueHelper;
        this.userService = userService;
        this.companyService = companyService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeRepository = employeRepository;
        this.rubriqueRepository = rubriqueRepository;
        this.rubriqueService = rubriqueService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.departementService = departementService;
        this.montantRubriqueRepository = montantRubriqueRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MontantRubriqueDTO>>> getAll() {
        List<MontantRubriqueDTO> list = montantRubriqueService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des montants rubriques récupérée", list));
    }

    @GetMapping("/entreprise/liste/{companyId}")
    public ResponseEntity<ApiResponse<List<MontantRubriqueDTO>>> getByCompanyId(@PathVariable Long companyId) {
        List<MontantRubriqueDTO> list = montantRubriqueService.findByCompanyId(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des montants rubriques de l'entreprise récupérée", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MontantRubriqueDTO>> getById(@PathVariable Long id) {
        Optional<MontantRubriqueDTO> opt = montantRubriqueService.getById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Montant rubrique trouvé", opt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Montant rubrique non trouvé", null));
    }

    @PostMapping("/entreprise/filtre")
    public ResponseEntity<?> getMontantRubriqueParEntrepriseFiltre(@RequestBody MontantRubriqueRequestDto requestDto) {
        try {
            // Vérification de l'entreprise
            Optional<Company> companyOpt = companyService.findById(requestDto.getCompanyId());
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Company company = companyOpt.get();

            // Vérification des paramètres obligatoires
            if ( requestDto.getMois() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Paramètres mois  obligatoire", null));
            }

            // Récupération des employés selon département si précisé
            List<EmployeDTO> employes;

            employes = employeService.getEmployesByEntreprise(company.getId());

            // Construction de la liste des DTO
//            List<MontantRubriqueDTO> result = employes.stream()
//                    .map(employe -> buildMontantRubriqueDTOFiltre(employe, requestDto))
//                    .filter(Objects::nonNull)
//                    .toList();

            List<MontantRubriqueDTO> result = employes.stream()
                    .map(employe -> buildMontantRubriqueDTOFiltre(employe, requestDto))
                    .filter(Objects::nonNull)
                    .filter(dto -> {
                        String libelle = dto.getRubriqueName();
                        if (libelle == null) return true;
                        String libelleTrim = libelle.trim().toUpperCase();
                        return !libelleTrim.equals("SALAIRE 13E MOIS")
                                && !libelleTrim.equals("SALAIRE MOYEN")
                                && !libelleTrim.equals("PRIMES EXCEPTIONNELLES");
                    })
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Données récupérées avec succès", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }


    private MontantRubriqueDTO buildMontantRubriqueDTOFiltre(EmployeDTO employe, MontantRubriqueRequestDto requestDto) {
        ContractEmployeDTO contratActif = contratEmployeService.getActifDTOByEmployeId(employe.getId())
                .orElse(null);

        if (contratActif == null || !contratActif.getCompanyId().equals(requestDto.getCompanyId())) {
            // L’employé n’a pas de contrat actif avec cette entreprise → on ignore
            return null;
        }

        Optional<MontantRubriqueDTO> montant = montantRubriqueService
                .findByEmployeIdAndMoisRubriqueFiltre(
                        employe.getId(),
                        requestDto.getMois());

        MontantRubriqueDTO dto = new MontantRubriqueDTO();
        dto.setEmploye(employe);
        dto.setMoisRubrique(requestDto.getMois());
        // Récupérer le libellé de la rubrique
        dto.setContratEmployeId(contratActif.getId());
        dto.setCompanyId(contratActif.getCompanyId());


        if (montant.isPresent()) {
            dto.setId(montant.get().getId());
            dto.setRubriqueName(montant.get().getRubriqueName());
            dto.setMontantRubrique(montant.get().getMontantRubrique());
        } else {
            dto.setId(null);  // 0L ça veut rien dire, mieux vaut null
            dto.setMontantRubrique(BigDecimal.ZERO);
        }
        return dto;
    }


    private MontantRubriqueDTO buildMontantRubriqueDTO(EmployeDTO employe, MontantRubriqueRequestDto requestDto) {

        YearMonth yearMonth1 = YearMonth.parse(requestDto.getMois()); // 2026-01
        LocalDate localDate = yearMonth1.atDay(1);

        ContractEmployeDTO contratActif = contratEmployeService.getActifDTOByEmployeIdMois(employe.getId(),localDate )
                .orElse(null);

        if (contratActif == null || !contratActif.getCompanyId().equals(requestDto.getCompanyId())) {
            // L’employé n’a pas de contrat actif avec cette entreprise → on ignore
            return null;
        }

        Optional<MontantRubriqueDTO> montant = montantRubriqueService
                .findByEmployeIdAndMoisRubriqueAndRubriqueId(
                        employe.getId(),
                        requestDto.getMois(),
                        requestDto.getRubriqueId());

        MontantRubriqueDTO dto = new MontantRubriqueDTO();
        dto.setEmploye(employe);
        dto.setMoisRubrique(requestDto.getMois());
        dto.setRubriqueId(requestDto.getRubriqueId());
        // Récupérer le libellé de la rubrique
        String libelleRubrique = rubriqueService.getById(requestDto.getRubriqueId())
                .map(Rubrique::getLibelle)  // adapte getLibelle() au nom réel de ton getter
                .orElse("Rubrique inconnue");
        dto.setRubriqueName(libelleRubrique);
        dto.setContratEmployeId(contratActif.getId());
        dto.setCompanyId(contratActif.getCompanyId());

        if (montant.isPresent()) {
            dto.setId(montant.get().getId());
            dto.setMontantRubrique(montant.get().getMontantRubrique());
        } else {
            dto.setId(null);  // 0L ça veut rien dire, mieux vaut null
            dto.setMontantRubrique(BigDecimal.ZERO);
        }
        return dto;
    }

    @PostMapping("/entreprise/nouveau")
    public ResponseEntity<?> getMontantRubriqueParEntreprise(@RequestBody MontantRubriqueRequestDto requestDto) {
        try {
            // Vérification de l'entreprise
            Optional<Company> companyOpt = companyService.findById(requestDto.getCompanyId());
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Company company = companyOpt.get();

            // Vérification des paramètres obligatoires
            if (requestDto.getRubriqueId() == null || requestDto.getMois() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Paramètres mois et rubriqueId obligatoires", null));
            }

            // Vérification de la rubrique
            boolean rubriqueExists = rubriqueService.existsById(requestDto.getRubriqueId());
            if (!rubriqueExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Rubrique non trouvée", null));
            }

            // Vérification du département s'il est fourni
            if (requestDto.getDepartementId() != null) {
                boolean departementExistsForCompany = departementService.existsByIdAndCompanyId(requestDto.getDepartementId(), company.getId());
                if (!departementExistsForCompany) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Département non trouvé dans l'entreprise", null));
                }
            }

            // Récupération des employés selon département si précisé
            List<EmployeDTO> employes;

            YearMonth yearMonth1 = YearMonth.parse(requestDto.getMois()); // 2026-01
            LocalDate localDate = yearMonth1.atDay(1);

            if (requestDto.getDepartementId() != null) {
                employes = employeService.getEmployesByEntrepriseAndDepartementMois(
                        company.getId(), requestDto.getDepartementId(),localDate);
            } else {
                employes = employeService.getEmployesByEntrepriseMois(company.getId(),localDate);
            }

            // Construction de la liste des DTO
            List<MontantRubriqueDTO> result = employes.stream()
                    .map(employe -> buildMontantRubriqueDTO(employe, requestDto))
                    .filter(Objects::nonNull)
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Données récupérées avec succès", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }



    @PostMapping("/entreprise/{companyId}/montants-rubriques")
    public ResponseEntity<?> saveOrUpdateMontantRubriques(
            @PathVariable Long companyId,
            @RequestBody List<MontantRubriqueDTO> montantRubriqueDTOList
    ) {
        try {

            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }

            Company company = companyOpt.get();

            List<MontantRubriqueDTO> result = new ArrayList<>();

            for (MontantRubriqueDTO dto : montantRubriqueDTOList) {
                Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getActifByEmployeIdAndCompanyId(dto.getEmploye().getId(),companyId);
                if (contratOpt.isEmpty()) {
                    continue;
                }
                ContractEmployeDTO contratActif = contratOpt.get();

                Optional<Rubrique> rubriqueOpt = rubriqueService.getById(dto.getRubriqueId());
                if (rubriqueOpt.isEmpty()) {
                    continue;
                }

                Rubrique rubrique = rubriqueOpt.get();

                Optional<MontantRubriqueDTO> existingOpt = montantRubriqueService.findByEmployeIdAndMoisRubriqueAndRubriqueId(
                        dto.getEmploye().getId(),
                        dto.getMoisRubrique(),
                        dto.getRubriqueId()
                );

                // VÉRIFICATION : Ignorer si montantRubrique est null ou égal à 0
                if (dto.getMontantRubrique() == null || dto.getMontantRubrique().compareTo(BigDecimal.ZERO) == 0) {
                    System.out.println("Montant rubrique est null ou 0");

                    // Si une entrée existe déjà, la supprimer
                    if (existingOpt.isPresent()) {
                        System.out.println("Suppression de l'entrée existante id=" + existingOpt.get().getId());
                        montantRubriqueRepository.deleteById(existingOpt.get().getId());
                    }

                    continue;
                }

                if (existingOpt.isPresent()) {
                    ContratEmploye contratEmploye = contratEmployeService.getById(contratActif.getId()).orElse(null).toEntity();
                    Employe employe = employeService.findById(dto.getEmploye().getId()).orElse(null);
                    User addedBy = null; // À gérer selon ton contexte

                    MontantRubrique existingEntity = montantRubriqueService.getById(existingOpt.get().getId())
                            .map(existingDto -> existingDto.toEntity(contratEmploye, employe, company, rubrique, addedBy))
                            .orElse(null);

                    if (existingEntity == null) {
                        continue;
                    }

                    existingEntity.setMontantRubrique(dto.getMontantRubrique());
                    existingEntity.setMoisRubrique(dto.getMoisRubrique());
                    MontantRubrique updatedEntity = montantRubriqueService.update(existingEntity, dto);
                    result.add(MontantRubriqueDTO.fromEntity(updatedEntity));
                } else {
                    Employe employe = employeService.findById(dto.getEmploye().getId()).orElse(null);
                    if (employe == null) {
                        continue;
                    }

                    MontantRubriqueDTO savedDto = montantRubriqueService.save(dto, contratActif.toEntity(), employe, company, rubrique, null);
                    result.add(savedDto);
                }
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Montants rubriques enregistrés", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement : " + e.getMessage(), null));
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody MontantRubriqueDTO dto) {
        try {
            List<ErrorResponse> errors = new ArrayList<>();
            User currentUser = userService.getCurrentUser();

            if (dto.getMontantRubrique() == null) {
                errors.add(new ErrorResponse("montantRubrique", "Le montant rubrique est requis."));
            }
            if (dto.getCompanyId() == null) {
                errors.add(new ErrorResponse("companyId", "L'entreprise est requise."));
            }
            if (dto.getContratEmployeId() == null) {
                errors.add(new ErrorResponse("contratEmployeId", "Le contrat employé est requis."));
            }
            if (dto.getEmployeId() == null) {
                errors.add(new ErrorResponse("employeId", "L'employé est requis."));
            }
            if (dto.getRubriqueId() == null) {
                errors.add(new ErrorResponse("rubriqueId", "La rubrique est requise."));
            }
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));
            }

            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(dto.getContratEmployeId());
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable."));
            }
            ContratEmploye contratEmploye = contratOpt.orElse(null);

            Optional<Employe> employeOpt = employeRepository.findById(dto.getEmployeId());
            if (employeOpt.isEmpty()) {
                errors.add(new ErrorResponse("employeId", "Employé introuvable."));
            }
            Employe employe = employeOpt.orElse(null);

            Company company = companyService.getById(dto.getCompanyId());
            if (company == null) {
                errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));
            }

            Optional<Rubrique> rubriqueOpt = rubriqueRepository.findById(dto.getRubriqueId());
            if (rubriqueOpt.isEmpty()) {
                errors.add(new ErrorResponse("rubriqueId", "Rubrique introuvable."));
            }
            Rubrique rubrique = rubriqueOpt.orElse(null);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entités référencées introuvables", errors));
            }

            MontantRubrique entity = dto.toEntity(contratEmploye, employe, company, rubrique, currentUser);

            List<ErrorResponse> validationErrors = montantRubriqueHelper.getInvalidFieldMessages(entity);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", validationErrors));
            }

            MontantRubriqueDTO saved = montantRubriqueService.save(dto, contratEmploye, employe, company, rubrique, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Montant rubrique créé avec succès", saved));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la création du montant rubrique", null));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!montantRubriqueService.exists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Montant rubrique non trouvé", null));
        }
        montantRubriqueService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Montant rubrique supprimé avec succès", null));
    }

    @GetMapping("/par-entreprise/{companyId}")
    public ResponseEntity<ApiResponse<List<MontantRubriqueDTO>>> getByCompany(@PathVariable Long companyId) {
        if (!companyService.existsById(companyId)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
        }
        List<MontantRubriqueDTO> list = montantRubriqueService.findByCompanyId(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des montants rubriques de l'entreprise", list));
    }
}
