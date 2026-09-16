package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.*;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.paie.AcompteService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/paie/acomptes")
public class AcompteController {

    private final AcompteService acompteService;
    private final UserService userService;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final CompanyRepository companyRepository;
    private final DepartementService departementService;

    public AcompteController(AcompteService acompteService, UserService userService, CompanyService companyService, EmployeService employeService, ContratEmployeService contratEmployeService, CompanyRepository companyRepository, DepartementService departementService) {
        this.acompteService = acompteService;
        this.userService = userService;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.companyRepository = companyRepository;
        this.departementService = departementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcompteDTO>>> getAll() {
        List<AcompteDTO> list = acompteService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des acomptes récupérée", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AcompteDTO>> getById(@PathVariable Long id) {
        Optional<AcompteDTO> opt = acompteService.getById(id);
        return opt.map(dto -> ResponseEntity.ok(new ApiResponse<>(true, "Acompte trouvée", dto)))
                .orElseGet(() -> new ResponseEntity<>(new ApiResponse<>(false, "Acompte non trouvée", null), HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AcompteDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = new ArrayList<>();

            if (dto.getEmployeId() == null)
                errors.add(new ErrorResponse("employeId", "Le champ 'employeId' est requis."));
            if (dto.getCompanyId() == null)
                errors.add(new ErrorResponse("companyId", "Le champ 'companyId' est requis."));
            if (dto.getMontant() == null)
                errors.add(new ErrorResponse("montant", "Le montant  est requis."));

            if (dto.getMois() == null) {
                errors.add(new ErrorResponse("mois", "Le mois  est requis."));
            }

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));

            Optional<Employe> employeOpt = employeService.findById(dto.getEmployeId());

            Company company = companyService.getById(dto.getCompanyId());

            if (employeOpt.isEmpty())
                errors.add(new ErrorResponse("employeId", "Employé introuvable."));
            if (company == null)
                errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));

            Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getContratEmployeNonArreteParEmploye(employeOpt.get());

            if (contratOpt.isEmpty())
                errors.add(new ErrorResponse("contratEmployeInexistant", "Contrat employé introuvable."));

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));


            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            dto.setAddedById(currentUser.getId());

            YearMonth mois = dto.getMois();

            AcompteDTO saved = acompteService.save(dto, contratOpt.get().toEntity(), employeOpt.get(), company, currentUser);

            return new ResponseEntity<>(new ApiResponse<>(true, "Acompte créée avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de l'acompte", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!acompteService.exists(id))
            return new ResponseEntity<>(new ApiResponse<>(false, "Acompte non trouvée", null), HttpStatus.NOT_FOUND);

        acompteService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Acompte supprimée avec succès", null));
    }

    @GetMapping("/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<List<AcompteDTO>>> getByEmploye(@PathVariable Long employeId) {
        if (!employeService.existsById(employeId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Employé introuvable", null));

        List<AcompteDTO> list = acompteService.findByEmployeId(employeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des acomptes de l'employé", list));
    }

        @GetMapping("/par-entreprise/{companyId}")
        public ResponseEntity<ApiResponse<List<AcompteDTO>>> getByCompany(@PathVariable Long companyId) {
            if (!companyService.existsById(companyId))
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

            List<AcompteDTO> list = acompteService.findByCompanyId(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des acomptes de l'entreprise", list));
        }

    @GetMapping("/par-employe/{employeId}/mois")
    public ResponseEntity<?> getAcomptesByEmployeAndMois(
            @PathVariable Long employeId,
            @RequestParam String mois
    ) {
        try {
            // Vérification de l'existence de l'entreprise
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Employé introuvable avec l'ID : " + employeId, null));
            }

            // Conversion du mois (ex: "2025-07") vers YearMonth
            YearMonth yearMonth = YearMonth.parse(mois);

            // Appel du service
            Optional<AcompteDTO> dtos = acompteService.findByEmployeAndMois(employeId, String.valueOf(yearMonth));

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Acomptes récupérées avec succès", dtos)
            );

        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Format du mois invalide. Utilisez 'yyyy-MM'.", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des acomptes", null));
        }
    }

    @GetMapping("/par-entreprise/{companyId}/mois")
    public ResponseEntity<?> getAcomptesByCompanyAndMois(
            @PathVariable Long companyId,
            @RequestParam String mois
    ) {
        try {
            // Vérification de l'existence de l'entreprise
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Entreprise introuvable avec l'ID : " + companyId, null));
            }

            // Conversion du mois (ex: "2025-07") vers YearMonth
            YearMonth yearMonth = YearMonth.parse(mois);

            // Appel du service
            List<AcompteDTO> dtos = acompteService.findByCompanyAndMois(companyId, mois);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Acomptes récupérées avec succès", dtos)
            );

        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Format du mois invalide. Utilisez 'yyyy-MM'.", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des acomptes", null));
        }
    }


    @PostMapping("/entreprise/nouveau")
    public ResponseEntity<?> getAcompteParEntreprise(@RequestBody AcompteRequestDTO requestDto) {
        try {
            // Vérification de l'entreprise
            Optional<Company> companyOpt = companyService.findById(requestDto.getCompanyId());
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Company company = companyOpt.get();

            // Vérification des paramètres obligatoires
            if (requestDto.getMois() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Paramètres mois  obligatoire", null));
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
            if (requestDto.getDepartementId() != null) {
                employes = employeService.getEmployesByEntrepriseAndDepartement(
                        company.getId(), requestDto.getDepartementId());
            } else {
                employes = employeService.getEmployesByEntreprise(company.getId());
            }

            // Construction de la liste des DTO
            List<AcompteDTO> result = employes.stream()
                    .map(employe -> buildAcompteDTO(employe, requestDto))
                    .filter(Objects::nonNull)
                    .toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Données récupérées avec succès", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }
    private AcompteDTO buildAcompteDTO(EmployeDTO employe, AcompteRequestDTO requestDto) {
        ContractEmployeDTO contratActif = contratEmployeService.getActifDTOByEmployeId(employe.getId())
                .orElse(null);

        if (contratActif == null || !contratActif.getCompanyId().equals(requestDto.getCompanyId())) {
            // L’employé n’a pas de contrat actif avec cette entreprise → on ignore
            return null;
        }

        Optional<AcompteDTO> acompte = acompteService
                .findByEmployeAndMois(
                        employe.getId(),
                        requestDto.getMois());

        AcompteDTO dto = new AcompteDTO();
        dto.setEmploye(employe);
        dto.setMois(YearMonth.parse(requestDto.getMois()));

        dto.setContratEmployeId(contratActif.getId());
        dto.setCompanyId(contratActif.getCompanyId());

        if (acompte.isPresent()) {
            dto.setId(acompte.get().getId());
            dto.setMontant(acompte.get().getMontant());
        } else {
            dto.setId(null);  // 0L ça veut rien dire, mieux vaut null
            dto.setMontant(BigDecimal.ZERO);
        }
        return dto;
    }

    @Transactional
    @PostMapping("/entreprise/{companyId}/acomptes")
    public ResponseEntity<?> saveOrUpdateMontantRubriques(
            @PathVariable Long companyId,
            @RequestBody List<AcompteDTO> acompteDTOList
    ) {
        try {
            System.out.println("Début traitement pour companyId = " + companyId);

            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                System.out.println("Entreprise non trouvée");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise non trouvée", null));
            }
            Company company = companyOpt.get();

            List<AcompteDTO> result = new ArrayList<>();

            for (AcompteDTO dto : acompteDTOList) {
                System.out.println("Traitement DTO : " + dto);
                Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getActifByEmployeIdAndCompanyId(dto.getEmploye().getId(),companyId);
                if (contratOpt.isEmpty()) {
                    System.out.println("Pas de contrat actif pour employé id=" + dto.getEmploye().getId() + " ou entreprise différente");
                    continue;
                }
                ContractEmployeDTO contratActif = contratOpt.get();

                Optional<AcompteDTO> existingOpt = acompteService.findByEmployeAndMois(
                        dto.getEmploye().getId(),
                        dto.getMois().toString() // Convertit YearMonth -> "2025-07"
                );

                if (existingOpt.isPresent()) {
                    System.out.println("Acompte existante trouvéee, mise à jour");

                    ContratEmploye contratEmploye = contratEmployeService.getById(contratActif.getId()).orElse(null).toEntity();
                    Employe employe = employeService.findById(dto.getEmploye().getId()).orElse(null);
                    User addedBy = null; // À gérer selon ton contexte

                    Acompte existingEntity = acompteService.getById(existingOpt.get().getId())
                            .map(existingDto -> existingDto.toEntity(contratEmploye, employe, company, addedBy))
                            .orElse(null);

                    if (existingEntity == null) {
                        System.out.println("Entity existante introuvable pour id=" + existingOpt.get().getId());
                        continue;
                    }

                    existingEntity.setMontant(dto.getMontant());
                    existingEntity.setMoisFromYearMonth(dto.getMois());
//                  existingEntity.setEmploye(employe);
                    Acompte updatedEntity = acompteService.update(existingEntity, dto);
                    result.add(AcompteDTO.fromEntity(updatedEntity));

                } else {
                    System.out.println("Nouvelle création de MontantRubrique");

                    Employe employe = employeService.findById(dto.getEmploye().getId()).orElse(null);
                    if (employe == null) {
                        System.out.println("Employé introuvable pour id=" + dto.getEmploye().getId());
                        continue;
                    }

                    AcompteDTO savedDto = acompteService.save(dto, contratActif.toEntity(), employe, company,  null);
                    result.add(savedDto);
                }
            }

            System.out.println("Nombre d'éléments traités : " + result.size());
            return ResponseEntity.ok(new ApiResponse<>(true, "Acomptes enregistrées", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement : " + e.getMessage(), null));
        }
    }

}
